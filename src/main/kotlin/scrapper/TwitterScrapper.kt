package scrapper

import models.Tweet
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import parser.TwitterParser

class TwitterScrapper : PageScrapper(TwitterParser()) {

    private val map = mutableMapOf<String, Tweet>()
    override fun shouldProcess(count: Int): Boolean {
        return true
    }

    override fun parseToCsv(pageSource: String?, url: String, count: Int): String? {

        // download page source
        val section: WebElement = driver.findElement(By.cssSelector("section[role='region']"))
        val html = section.getAttribute("innerHTML")

        // parse
        if (parser is TwitterParser) {
            val items = parser.parseTweets(html)
            for (item in items) {
                if (!map.containsKey(item.id)) {
                    map[item.id] = item
                }
            }

            // write to file
            val sorted = map.values.sortedByDescending { i -> i.instant }
            val path = Util.getPath(url, count, "csv").toString()
            Util.writeCsvRows(sorted, path)
            return path
        }

        return null
    }
}