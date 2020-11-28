import org.openqa.selenium.By

class FbPageScrapper: PageScrapper() {

    private var hideModalSuccess: Boolean = false

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