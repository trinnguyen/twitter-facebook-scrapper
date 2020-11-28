import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver

class FbPageScrapper: PageScrapper {
    override fun exec(url: String, number: Int): List<String> {
        val driver: WebDriver = FirefoxDriver()
        val list = mutableListOf<String>();

        try {
            driver.get(url);

            // wait for page to load
            Thread.sleep(2000);
            driver.manage().window().maximize()

            // scroll and save to files
            val js = driver as JavascriptExecutor

            var hideModalSuccess = false;
            var count = 0;
            while (true) {

                // try hiding modal
                if (!hideModalSuccess) {
                    hideModalSuccess = tryExec { driver.findElement(By.id("expanding_cta_close_button")).click() };
                }

                count ++
                val y = count * 2000
                js.executeScript("window.scrollBy(0,$y)")
                Thread.sleep(2000)

                // save data every 1 scrolls
                if (count % 5 == 0) {
                    list.add(Util.writeToFile(driver.pageSource, url, count))
                }

                // scroll
                if (count == number) {
                    break
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            driver.quit()
        }

        return list
    }


    fun tryExec(function: () -> Unit): Boolean {
        try {
            function.invoke()
            return true;
        } catch (ex: java.lang.Exception) {}
        return false;
    }
}