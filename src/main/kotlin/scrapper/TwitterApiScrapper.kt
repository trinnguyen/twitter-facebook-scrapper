package scrapper

import ULogger
import ULogger.logError
import ULogger.logException
import ULogger.logInfo
import Util
import models.Tweet
import parser.TwitterParser
import twitter4j.Paging
import twitter4j.Status
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import java.nio.file.Files
import java.nio.file.Paths


class TwitterApiScrapper : PageScrapper(TwitterParser()){

    override fun getSource(): String {
        return ""
    }

    override fun exec(url: String): String? {

        // read config file
        val cb = updateTokens() ?: return null

        val twitter = TwitterFactory(cb.build()).instance
        var pageno = 1
        val user = "samsung"
        val list = mutableListOf<Tweet>()
        val path = Util.generatePathFromUrl(url, Util.generateFileNameByTime("csv")) ?: return null
        while (true) {
            try {
                val page = Paging(pageno++, 100)
                val items = twitter.getUserTimeline(user, page)
                logInfo("new items: ${items.size}")
                if (items.isEmpty()) {
                    logInfo("no new item is found")
                    break
                }

                // update
                val rows = items.map { i -> toCsvRow(i) }
                list.addAll(rows)
                logInfo("total ${list.size}")

                // append
                Util.appendCsvRowsToFile(rows, path)
            } catch (ex: TwitterException) {
                logException(ex)
                break
            }
        }

        return path
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
        return Tweet(status.id.toString(), status.text, instant, "", status.retweetCount.toString(), status.favoriteCount.toString())
    }
}