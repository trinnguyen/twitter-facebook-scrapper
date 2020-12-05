package scrapper

import models.Tweet
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import parser.TwitterParser

class TwitterScrapper : PageScrapper(TwitterParser()) {

    override fun getSource(): String {
        val section: WebElement = driver.findElement(By.cssSelector("section[role='region']"))
        return section.getAttribute("innerHTML")
    }

}