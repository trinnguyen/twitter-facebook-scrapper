import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver

abstract class PageScrapper {
    protected val driver: WebDriver = FirefoxDriver()
    fun exec(url: String, number: Int): List<String> {
        println("start scrapping: $url")
        beforeStarting()
        val list = mutableListOf<String>();
        try {
            driver.get(url);

            // wait for page to load
            Thread.sleep(2000);

            // scroll and save to files
            val js = driver as JavascriptExecutor
            var count = 0;
            while (true) {

                // try hiding modal
                onScrolling()

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

    protected open fun beforeStarting() {}
    protected abstract fun onScrolling()
}