import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UtilTest {

    @Test
    fun generatePathFromFile() {
        assertEquals("/home/tmp/a.csv", Util.generatePathFromFile("/home/tmp/a.html", "csv"))
        assertEquals("src-gen/a.csv", Util.generatePathFromFile("src-gen/a.html", "csv"))
        assertEquals(null, Util.generatePathFromFile("", "csv"))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "https://facebook.com/apple, posts.csv, src-gen/facebook_com/apple/posts.csv",
            "https://twitter.com/apple, tweets.csv, src-gen/twitter_com/apple/tweets.csv"
        ]
    )
    fun generatePathFromUrl(url: String, name: String, expected: String) {
        assertEquals(expected, Util.generatePathFromUrl(url, name))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "https://www.twitter.com/samsung, true",
            "https://www.twitter.com/apple, true",
            "https://facebook.com/apple, false"
        ]
    )
    fun isTwitterPath(input: String, result: Boolean) {
        assertEquals(result, Util.isTwitterUrl(input))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "https://www.twitter.com/samsung, src-gen/www_twitter_com/samsung/samsung.log",
            "https://facebook.com/apple, src-gen/facebook_com/apple/apple.log",
            "src-gen/twitter_com/apple/tweets.html, src-gen/twitter_com/apple/tweets.log",
            "src-gen/facebook_com/apple/posts.html, src-gen/facebook_com/apple/posts.log",
        ]
    )
    fun generateLogPath(input: String, result: String) {
        assertEquals(result, Util.generateLogPath(input))
    }
}