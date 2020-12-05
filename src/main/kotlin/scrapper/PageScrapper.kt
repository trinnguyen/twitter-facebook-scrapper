package scrapper

import ULogger.logInfo
import Util
import models.CsvRow
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import parser.PageParser

abstract class PageScrapper(val parser: PageParser) {
    protected lateinit var driver: WebDriver

    open fun exec(url: String, provider: () -> WebDriver): String? {
        this.driver = provider()
        val cachedIds = mutableSetOf<String>()
        val path = Util.generatePathFromUrl(url, Util.generateFileNameByTime("csv")) ?: return null

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
                        logInfo("no items parsed after 30 attempts, finishing")
                        return path
                    }
                }

                // scroll to next
                scroll(js)
                Thread.sleep(getDelayMiliSeconds())
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
             this.driver.quit()
        }

        return path
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
        logInfo("found ${newItems.size} new items")
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