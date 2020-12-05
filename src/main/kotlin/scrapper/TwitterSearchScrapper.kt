package scrapper

import ULogger.logException
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import parser.TwitterSearchParser
import java.lang.Exception
import java.net.URI

class TwitterSearchScrapper: PageScrapper(TwitterSearchParser()) {

    override fun normalizeUrl(url: String): String {
        try {
            val username = URI(url).path.split("/").lastOrNull()?.trim('/')
            return "https://twitter.com/search?q=(from%3A$username)&src=typed_query&f=live";
        } catch (ex: Exception) {
            logException(ex)
        }

        throw IllegalArgumentException("invalid Twitter page url")
    }

    override fun getSource(): String {
        val section: WebElement = driver.findElement(By.cssSelector("section[role='region']"))
        return section.getAttribute("innerHTML")
    }
}