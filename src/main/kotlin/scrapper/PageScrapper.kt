package scrapper

import ULogger.logException
import ULogger.logInfo
import Util
import models.CsvRow
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import parser.PageParser
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

abstract class PageScrapper(val parser: PageParser) {
    protected lateinit var driver: WebDriver

    open fun exec(url: String, provider: () -> WebDriver): String? {
        this.driver = provider()
        val cachedIds = mutableSetOf<String>()
        val baseName = Util.generateFileNameByTime()
        val path = Util.generatePathFromUrl(url, "$baseName.csv") ?: return null

        logInfo("start scrapping $url to $path")

        beforeStarting()
        try {
            this.driver.get(normalizeUrl(url));

            // wait for page to load
            Thread.sleep(2000)

            // scroll and save to files
            val js = this.driver as JavascriptExecutor
            var countFailed = 0
            while (true) {

                // hide modal if needed
                onScrolling()

                // process
                if (processSource(cachedIds, path)) {
                    countFailed = 0
                } else {
                    countFailed++
                    if (countFailed == 30) {
                        logInfo("no items parsed after 30 attempts, finish with total ${cachedIds.size} items")
                        onFinishing(url, baseName)
                        return path
                    }
                }

                // scroll to next
                scroll(js)
                Thread.sleep(getDelayMiliSeconds())
            }

        } catch (ex: Exception) {
            logException(ex)
        } finally {
             this.driver.quit()
        }

        return path
    }

    private fun onFinishing(url: String, baseName: String) {
        if (driver is TakesScreenshot) {
            try {
                val tmpFile = (driver as TakesScreenshot).getScreenshotAs(OutputType.FILE)
                Util.generatePathFromUrl(url, "$baseName.png")?.let {
                    Files.move(tmpFile.toPath(), Paths.get(it))
                    logInfo("took screenshot to $it")
                }
            } catch (ex: Exception) {
                logException(ex)
            }
        }
    }

    open fun getDelayMiliSeconds(): Long {
        return 1500
    }

    protected open fun scroll(js: JavascriptExecutor) {
        js.executeScript("window.scrollBy(0,document.body.scrollHeight)")
    }

    protected open fun normalizeUrl(url: String): String {
        return url
    }

    private fun processSource(cachedIds: MutableSet<String>, path: String): Boolean {

        // process every single scroll
        val items: List<CsvRow> = parser.parseToRows(getSource())
        val newItems = items.filter { i -> !cachedIds.contains(i.id) }.sortedByDescending { i -> i.instant }
        logInfo("found ${newItems.size} new items, ${items.size - newItems.size} old items")
        if (newItems.isEmpty())
            return false

        // cache items
        newItems.forEach { i ->
            cachedIds.add(i.id)
            logInfo(i.formatCsv())
        }

        // append to csv file
        logInfo("total ${cachedIds.size}")
        Util.appendCsvRowsToFile(newItems, path)
        return true
    }

    abstract fun getSource(): String;

    protected open fun beforeStarting() {}

    protected open fun onScrolling() {}
}