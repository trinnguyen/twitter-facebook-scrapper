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
            "src-gen/twitter_com/apple/tweets.html, true",
            "src-gen/facebook_com/apple/posts.html, false",
            "https://facebook.com/apple, false"
        ]
    )
    fun isTwitterPath(input: String, result: Boolean) {
        assertEquals(result, Util.isTwitterPath(input))
    }
}