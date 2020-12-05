package scrapper

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import parser.TwitterParser

open class TwitterScrapper : PageScrapper(TwitterParser()) {

    override fun getSource(): String {
        val section: WebElement = driver.findElement(By.cssSelector("section[role='region']"))
        return section.getAttribute("innerHTML")
    }

    override fun scroll(js: JavascriptExecutor) {
        js.executeScript("window.scrollBy(0,600)")
    }

    override fun getDelayMiliSeconds(): Long {
        return 1000
    }
}