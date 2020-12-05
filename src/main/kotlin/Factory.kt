import models.ProcessType
import parser.FbPageParser
import parser.PageParser
import parser.TwitterParser
import parser.TwitterSearchParser
import scrapper.*
import java.lang.Exception


object Factory {
    fun createScrapper(type: ProcessType, url: String) : PageScrapper {
        return when (type) {
            ProcessType.Facebook -> FbPageScrapper()
            ProcessType.Twitter -> TwitterScrapper()
            ProcessType.TwitterApi -> TwitterApiScrapper()
            ProcessType.TwitterSearch -> TwitterSearchScrapper()
            else -> when {
                Util.isMobileFacebookUrl(url) -> FbPageScrapper()
                Util.isTwitterUrl(url) -> TwitterSearchScrapper()
                else -> FbPageScrapper()
            }
        }
    }

    fun createParser(type: ProcessType) : PageParser {
        return when (type) {
            ProcessType.Facebook -> FbPageParser()
            ProcessType.Twitter -> TwitterParser()
            ProcessType.TwitterApi -> throw Exception("not supported")
            else -> TwitterSearchParser()
        }
    }
}