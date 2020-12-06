import models.ProcessType
import org.apache.commons.cli.CommandLine
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import parser.FbPageParser
import parser.PageParser
import parser.TwitterParser
import scrapper.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


object Factory {
    fun createScrapper(cli: CommandLine, url: String) : PageScrapper {
        val speed = cli.getTwitterScrollingSpeed()
        return when (cli.getProcessType()) {
            ProcessType.Facebook -> FbPageScrapper()
            ProcessType.Twitter -> TwitterScrapper(speed)
            ProcessType.TwitterApi -> TwitterApiScrapper()
            ProcessType.TwitterSearch -> TwitterSearchScrapper(speed)
            else -> when {
                Util.isMobileFacebookUrl(url) -> FbPageScrapper()
                Util.isTwitterUrl(url) -> TwitterSearchScrapper(speed)
                else -> FbPageScrapper()
            }
        }
    }

    fun createParser(type: ProcessType) : PageParser {
        return when (type) {
            ProcessType.Facebook -> FbPageParser()
            ProcessType.Twitter -> TwitterParser()
            ProcessType.TwitterApi -> throw Exception("not supported")
            else -> TwitterParser()
        }
    }

    fun createWebDriver(cli: CommandLine, folder: File): (() -> WebDriver) {
        if (cli.isChromeDriver()) {
            val child = Paths.get(folder.absolutePath, "chromedriver")
            if (!Files.exists(child)) {
                ULogger.logInfo("chromedriver is not exist, auto download to $child")
                if (!Util.downloadChromeDriver(folder)) {
                    throw Exception("cannot download chromedriver")
                }
            }

            return {
                ChromeDriver()
            }
        } else {
            val child = Paths.get(folder.absolutePath, "geckodriver")
            if (!Files.exists(child)) {
                ULogger.logInfo("geckodriver is not exist, auto download to $child")
                if (!Util.downloadGeckoDriver(folder)) {
                    throw Exception("cannot download geckodriver")
                }
            }

            val profileName = cli.getOptionValue('p', "FbScrapper")
            System.setProperty("webdriver.firefox.driver", child.toAbsolutePath().toString());
            System.setProperty("webdriver.firefox.profile", profileName);
            return {
                FirefoxDriver()
            }
        }
    }
}