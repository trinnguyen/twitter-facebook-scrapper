package scrapper

import models.CsvRow
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import parser.PageParser
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

abstract class PageScrapper(val parser: PageParser) {
    protected val driver: WebDriver = FirefoxDriver()

    val queue = ArrayDeque<String>()

    fun exec(url: String, countCsv: Int): String? {
        println("start scrapping: $url")
        beforeStarting()
        queue.clear()
        try {
            driver.get(url);

            // wait for page to load
            Thread.sleep(2000);

            // scroll and save to files
            val js = driver as JavascriptExecutor
            var count = 1
            while (true) {

                // hide modal if needed
                processSource(url, count, countCsv)
                onScrolling()

                // scroll to next
                val y = count++ * 2000
                js.executeScript("window.scrollBy(0,$y)")
                Thread.sleep(1500)
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            driver.quit()
        }

        return queue.last
    }

    private fun processSource(url: String, count: Int, countCsv: Int) {
        if (shouldProcess(count)) {
            val result: String? = parseToCsv(driver.pageSource, url, count)
            if (!result.isNullOrEmpty()) {
                // delete last csv file
                queue.add(result)
                if (queue.size > countCsv) {
                    val tmpFile = queue.remove()
                    try {
                        Files.delete(Paths.get(tmpFile))
                        println("deleted csv: $tmpFile")
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
    }

    abstract fun shouldProcess(count: Int): Boolean

    abstract fun parseToCsv(pageSource: String?, url: String, count: Int): String?;

    protected open fun beforeStarting() {}
    protected open fun onScrolling() {}
}