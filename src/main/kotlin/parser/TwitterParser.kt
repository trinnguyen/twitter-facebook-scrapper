package parser

import models.CsvRow
import models.Tweet
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.Instant


open class TwitterParser: PageParser() {

    fun parseTweets(source: String): List<Tweet> {
        try {
            val list = mutableListOf<Tweet>()
            // get
            val doc = Jsoup.parse(source)
            val elements = doc.select("div[data-testid='tweet']")
            for (tweet in elements) {
                try {
                    val id: String = getStatusId(tweet) ?: continue
                    val date = tweet.select("time").attr("datetime")
                    val text = tweet.select("div[lang]").text()
                    val replies = tweet.select("div[data-testid='reply']").text()
                    val retweets = tweet.select("div[data-testid='retweet']").text()
                    val likes = tweet.select("div[data-testid='like']").text()

                    val item = Tweet(id, text, Instant.parse(date), replies, retweets, likes)
                    list.add(item)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            return list
        } catch (ex: Exception) {
            ex.printStackTrace()
            return emptyList()
        }
    }

    private fun getStatusId(tweet: Element): String? {
        val links = tweet.select("a[role='link']")
        return links.firstOrNull { l -> l.attr("href").contains("/status/") }?.attr("href")
    }

    override fun parseToRows(source: String): List<CsvRow> {
        return parseTweets(source)
    }
}