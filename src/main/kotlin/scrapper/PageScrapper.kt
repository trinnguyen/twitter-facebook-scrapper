package scrapper

import ULogger.logInfo
import models.CsvRow
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import parser.PageParser

abstract class PageScrapper(val parser: PageParser) {
    protected val driver: WebDriver = FirefoxDriver()

    fun exec(url: String): String? {
        val cachedIds = mutableSetOf<String>()
        val fileName = Util.generateFileNameByTime("csv")
        val path = Util.generatePathFromUrl(url, fileName) ?: return null

        logInfo("start scrapping $url to $path")

        beforeStarting()
        try {
            driver.get(url);

            // wait for page to load
            Thread.sleep(2000);

            // scroll and save to files
            val js = driver as JavascriptExecutor
            var count = 1
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
                val y = count++ * 2000
                js.executeScript("window.scrollBy(0,$y)")
                Thread.sleep(1500)
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            driver.quit()
        }

        return path
    }

    private fun processSource(cachedIds: MutableSet<String>, path: String): Boolean {

        // process every single scroll
        val items: List<CsvRow> = parser.parseToRows(getSource())
        val newItems = items.filter { i -> !cachedIds.contains(i.id) }.sortedByDescending { i -> i.instant }
        logInfo("found ${newItems.size} new items, total: ${cachedIds.size + newItems.size} ")
        if (newItems.isEmpty())
            return false

        // cache items
        newItems.forEach { i ->
            cachedIds.add(i.id)
            logInfo(i.formatCsv())
        }

        // append to csv file
        Util.appendCsvRowsToFile(newItems, path)
        return true
    }

    abstract fun getSource(): String;

    protected open fun beforeStarting() {}

    protected open fun onScrolling() {}
}