package scrapper

import ULogger.logError
import ULogger.logException
import ULogger.logInfo
import Util
import models.Tweet
import org.openqa.selenium.WebDriver
import parser.TwitterParser
import twitter4j.*
import twitter4j.conf.ConfigurationBuilder
import java.nio.file.Files
import java.nio.file.Paths


class TwitterApiScrapper : PageScrapper(TwitterParser()){

    override fun getSource(): String {
        return ""
    }

    override fun exec(url: String, provider: () -> WebDriver): String? {

        // read config file
        val cb = updateTokens() ?: return null
        val user = Util.getLastSegment(url) ?: return null
        val path = Util.generatePathFromUrl(url, Util.generateFileNameByTime("csv")) ?: return null

        val twitter = TwitterFactory(cb.build()).instance
        loadTimeline(twitter, user, path)

        return path
    }

    private fun loadTimeline(twitter: Twitter, user: String, path: String) {
        val ids = mutableSetOf<String>()
        var pageno = 1
        while (true) {
            try {
                val page = Paging(pageno++, 100)
                val items = twitter.getUserTimeline(user, page)

                // update
                val rows = items.map { i -> toCsvRow(i) }.filter { i -> !ids.contains(i.id) }
                logInfo("new items: ${rows.size}")
                if (rows.isEmpty()) {
                    break
                }

                // catch
                rows.forEach { i -> ids.add(i.id) }

                // append
                logInfo("total ${ids.size}")
                Util.appendCsvRowsToFile(rows, path)
            } catch (ex: TwitterException) {
                logException(ex)
                break
            }
        }
    }

    private fun updateTokens(): ConfigurationBuilder? {
        val path = Paths.get("tokens.txt")
        if (!path.toFile().exists()) {
            logError("missing tokens in file: $path. Required keys in line-by-line: \nAPI key \nAPI secret key \nAccess token\nAccess token secret")
            return null
        }

        try {
            val lines: List<String> = Files.readAllLines(path)
            if (lines.size < 4) {
                logError("missing tokens in file: $path. Required keys in line-by-line: \nAPI key \nAPI secret key \nAccess token\nAccess token secret")
                return null
            }

            val cb = ConfigurationBuilder()
            cb.setOAuthConsumerKey(lines[0])
            cb.setOAuthConsumerSecret(lines[1])
            cb.setOAuthAccessToken(lines[2])
            cb.setOAuthAccessTokenSecret(lines[3])
            return cb

        } catch (ex: Exception) {
            logException(ex)
        }

        return null
    }

    private fun toCsvRow(status: Status): Tweet {
        val instant = status.createdAt.toInstant()
        return Tweet(
            status.id.toString(),
            status.text.replace("\n"," ").replace(System.lineSeparator(), " "),
            instant,
            "",
            status.retweetCount.toString(),
            status.favoriteCount.toString()
        )
    }
}