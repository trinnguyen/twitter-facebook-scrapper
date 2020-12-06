package scrapper

import ULogger.logException
import java.lang.Exception
import java.net.URI

class TwitterSearchScrapper(speed: Long): TwitterScrapper(speed) {

    override fun normalizeUrl(url: String): String {
        try {
            val username = URI(url).path.split("/").lastOrNull()?.trim('/')
            return "https://twitter.com/search?q=(from%3A$username)%20-filter%3Areplies&src=typed_query&f=live"
        } catch (ex: Exception) {
            logException(ex)
        }

        throw IllegalArgumentException("invalid Twitter page url")
    }
}