package scrapper

import Util
import org.openqa.selenium.By
import parser.FbPageParser

class FbPageScrapper : PageScrapper(FbPageParser()) {

    private var hideModalSuccess: Boolean = false
    override fun shouldProcess(count: Int): Boolean {
        return count % 5 == 0
    }

    override fun parseToCsv(pageSource: String?, url: String, count: Int): String? {
        if (pageSource.isNullOrBlank())
            return null

        try {
            val filePath = Util.generatePath(url, count, "csv")
            if (filePath != null) {
                if (parser.parseHtmlFileToCsv(pageSource, filePath)) {
                    return filePath
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }


        return null
    }

    override fun beforeStarting() {
        super.beforeStarting()
        hideModalSuccess = false
    }

    override fun onScrolling() {
        if (!hideModalSuccess) {
            hideModalSuccess = Util.tryExec { driver.findElement(By.id("expanding_cta_close_button")).click() };
        }
    }
}