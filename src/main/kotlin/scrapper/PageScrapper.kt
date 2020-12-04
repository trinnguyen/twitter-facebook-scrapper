package scrapper

import Util
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import parser.PageParser
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

abstract class PageScrapper(private val parser: PageParser) {
    protected val driver: WebDriver = FirefoxDriver()

    fun exec(url: String, countCsv: Int): String? {
        println("start scrapping: $url")
        beforeStarting()
        val queue = ArrayDeque<String>()
        try {
            driver.get(url);

            // wait for page to load
            Thread.sleep(2000);

            // scroll and save to files
            val js = driver as JavascriptExecutor
            var count = 1
            while (true) {

                // parse data
                if (count % 5 == 0) {
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

                // hide modal if needed
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

    private fun parseToCsv(pageSource: String?, url: String, count: Int): String? {
        if (pageSource.isNullOrBlank())
            return null

        try {
            val csvPath = Util.getPath(url, count, "csv")
            val filePath = csvPath.toAbsolutePath().toString()
            if (parser.parseToCsv(pageSource, filePath))
                return filePath
        } catch (ex: Exception) {
            ex.printStackTrace()
        }


        return null
    }

    protected open fun beforeStarting() {}
    protected abstract fun onScrolling()
}