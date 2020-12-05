import parser.FbPageParser
import parser.MobileFacebookParser
import parser.PageParser
import parser.TwitterParser
import scrapper.*


object Factory {
    fun createScrapper(url: String) : PageScrapper {
        return when {
            Util.isTwitterUrl(url) -> TwitterApiScrapper()
            Util.isMobileFacebookUrl(url) -> MobileFacebookScrapper()
            else -> FbPageScrapper()
        }
    }

    fun createParser(provider: String) : PageParser {
        return when (provider) {
            "twitter" -> TwitterParser()
            "mbasic" -> MobileFacebookParser()
            else -> FbPageParser()
        }
    }
}