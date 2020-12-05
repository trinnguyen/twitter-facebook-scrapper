import Util.normalizeNumber
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.nio.file.Paths

class UtilTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "/home/tmp/a.html, /home/tmp/a.csv",
            "src-gen/a.html, src-gen/a.csv"
        ]
    )
    fun generatePathFromFile(input: String, expected: String) {
        val result = Util.generatePathFromFile(input, "csv")
        assertNotNull(result)
        assertEquals(Paths.get(expected).toAbsolutePath().toString(), Paths.get(result).toAbsolutePath().toString())
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
    fun generateLogPath(input: String, expected: String) {
        assertEquals(
            Paths.get(expected).toAbsolutePath().toString(),
            Paths.get(Util.generateLogPath(input)).toAbsolutePath().toString())
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1, 1",
            "0, 0",
            "1K, 1000",
            "2K, 2000",
            "10K, 10000",
            "11.76K, 11760",
            "1.76K, 1760",
            "210K, 210000",
            "1M, 1000000",
            "4.3M, 4300000",
        ]
    )
    fun normalizeNumber(input: String, expected: String) {
        assertEquals(expected, input.normalizeNumber())
    }

}