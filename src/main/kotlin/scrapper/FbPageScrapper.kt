package scrapper

import Util
import org.openqa.selenium.By
import parser.FbPageParser
import java.nio.file.Files
import java.nio.file.Paths

class FbPageScrapper : PageScrapper(FbPageParser()) {

    private var hideModalSuccess: Boolean = false
    override fun shouldProcess(count: Int): Boolean {
        return count % 5 == 0
    }

    override fun parseToCsv(pageSource: String?, url: String, count: Int): String? {
        if (pageSource.isNullOrBlank())
            return null

        try {
            val csvPath = Util.getPath(url, count, "csv")
            val filePath = csvPath.toAbsolutePath().toString()
            if (parser.parseToCsv(pageSource, filePath)) {
                return filePath
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