import ULogger.logException
import ULogger.logInfo
import models.CsvRow
import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object Util {
    private const val FirefoxDriverPath = "https://github.com/mozilla/geckodriver/releases/download/v0.28.0/geckodriver-v0.28.0-macos.tar.gz"

    private const val ChromeDriverPath = "https://chromedriver.storage.googleapis.com/87.0.4280.88/chromedriver_mac64.zip"

    fun downloadGeckoDriver(folder: File): Boolean {
        val filename = "geckodriver.tar.gz"
        val cmdList = listOf("/bin/sh", "-c", "curl -L $FirefoxDriverPath -o $filename && tar -xf $filename")
        return downloadDriverDriver(cmdList, folder)
    }

    fun downloadChromeDriver(folder: File): Boolean {
        val filename = "chromedriver_mac64.zip"
        val cmdList = listOf("/bin/sh", "-c", "curl -L $ChromeDriverPath -o $filename && unzip $filename")
       return downloadDriverDriver(cmdList, folder)
    }

    fun downloadDriverDriver(cmdList: List<String>, folder: File): Boolean {
        val result = ProcessBuilder(cmdList)
            .directory(folder)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()

        if (result != 0)
            return false

        if (result == 0) {
            logInfo("Successfully download and extract geckodriver")
        }

        return true
    }

    /**
     * generate path for file
     */
    fun generatePathFromFile(file: String, ext: String): String? {
        val path = Paths.get(file)
        val f = path.toAbsolutePath().toFile()

        val baseName = f.nameWithoutExtension
        return f.parentFile?.resolve("$baseName.$ext")?.toString()
    }

    /**
     * generate path from URL
     * https://facebook_com/apple => src-gen/facebook_com/apple.ext
     */
    fun generatePathFromUrl(url: String, filename: String): String? {
        try {
            val uri = URI(url)
            val path = Paths.get(
                "src-gen",
                uri.host.replace(".", "_"),
                uri.path.trim('/').replace("/", "_"),
                filename
            )

            // create folder if needed
            val parent = path.toFile().parentFile;
            if (!parent.exists()) {
                parent.mkdirs()
            }

            return path.toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    fun generatePath(url: String, count: Int, ext: String): String? {
        val filename = "$count.$ext"
        return generatePathFromUrl(url, filename)
    }

    fun writeCsvRows(items: List<CsvRow>, csvPath: String) {
        val path = Paths.get(csvPath)
        val builder = StringBuilder()
        if (items.isNotEmpty()) {
            builder.append(items.first().header()).append(System.lineSeparator())
        }
        items.forEach {
            builder.append(it.formatCsv()).append(System.lineSeparator())
        }

        Files.writeString(path, builder.toString())
        logInfo("wrote CSV to file $path")
    }

    fun appendCsvRowsToFile(items: List<CsvRow>, csvPath: String) {
        if (items.isEmpty())
            return

        val path = Paths.get(csvPath)
        val builder = StringBuilder()
        val isExist = path.toFile().exists()
        if (!isExist) {
            builder.append(items.first().header()).append(System.lineSeparator())
        }
        items.forEach {
            builder.append(it.formatCsv()).append(System.lineSeparator())
        }

        Files.writeString(path, builder.toString(), if (isExist) StandardOpenOption.APPEND else StandardOpenOption.CREATE)
        logInfo("appended items to CSV file $path")
        logInfo("")
    }

    fun isTwitterUrl(link: String): Boolean {
        if (isValidUrl(link)) {
            val uri = URI(link)
            return uri.host.endsWith("twitter.com", true)
        }

        return false
    }

    fun isMobileFacebookUrl(link: String): Boolean {
        if (isValidUrl(link)) {
            val uri = URI(link)
            return uri.host.equals("mbasic.facebook.com", true)
        }

        return false
    }

    fun isValidUrl(path: String): Boolean {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return try {
                val url = URL(path);
                url.toURI()
                true
            } catch (ex: URISyntaxException) {
                false
            }
        }

        return false
    }

    fun isHtmlFile(path: String): Boolean {
        return !isValidUrl(path) && (path.endsWith(".html") || path.endsWith(".htm"))
    }

    fun tryExec(function: () -> Unit): Boolean {
        try {
            function.invoke()
            return true;
        } catch (ex: Exception) {
        }
        return false;
    }

    fun generateLogPath(path: String): String? {
        if (isValidUrl(path)) {
            val lastPart = getLastSegment(path) ?: return null
            return generatePathFromUrl(path, "$lastPart.log")
        } else if (isHtmlFile(path)) {
            return generatePathFromFile(path, "log")
        }

        return null
    }

    fun getLastSegment(path: String): String? {
        try {
            val uri = URI(path)
            val segments = uri.path.split("/")
            return segments.lastOrNull()
        } catch (ex: Exception) {
            logException(ex)
        }

        return null
    }

    fun generateFileNameByTime(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        return formatter.format(LocalDateTime.now())
    }

    fun generateFileNameByTime(ext: String): String {
        return generateFileNameByTime() + "." + ext
    }

    /**
     * parse number with count
     * 43
     * 4.3K
     * 4.3M
     * 1M
     */
    fun String?.normalizeNumber(): String {
        if (this.isNullOrEmpty())
            return "0"

        try {

            if (this.endsWith("K") || this.endsWith("M")) {
                val value = this.substring(0, this.length - 1)

                // expect dot
                val float = value.toFloat()
                val multiply = if (this.endsWith("K")) 1000 else 1000000
                return (float * multiply).toLong().toString()
            }

            return this.toLong().toString()
        } catch (ex: Exception) {
            logException(ex)
        }

        return this
    }
}