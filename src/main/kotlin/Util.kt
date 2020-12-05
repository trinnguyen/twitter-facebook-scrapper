import models.CsvRow
import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


object Util {
    private const val DriverPath = "https://github.com/mozilla/geckodriver/releases/download/v0.28.0/geckodriver-v0.28.0-macos.tar.gz"

    fun downloadGeckoDriver(folder: File): Boolean {
        val filename = "geckodriver.tar.gz"
        val cmdList = listOf("/bin/sh", "-c", "curl -L $DriverPath -o $filename && tar -xf $filename")
        val result = ProcessBuilder(cmdList)
            .directory(folder)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()

        if (result != 0)
            return false

        if (result == 0) {
            println("Successfully download and extract geckodriver")
        }

        return true
    }

    fun getCsvFile(file: String): String? {
        val path = Paths.get(file)
        val f = path.toFile()
        if (!f.exists()) return null

        val baseName = f.nameWithoutExtension
        return Paths.get(f.parentFile.absolutePath, "$baseName.csv").toAbsolutePath().toString()
    }

    fun getPath(url: String, count: Int, ext: String): Path {
        val uri = URI(url)

        // path: src-gen/facebook_com/apple/
        val filename = "$count.$ext"
        val path = Paths.get(
            "src-gen",
            uri.host.replace(".", "_"),
            uri.path.trim('/').replace("/", "_"),
            filename)

        val parent = path.toFile().parentFile;
        if (!parent.exists()) {
            parent.mkdirs()
        }

        return path
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
        println("wrote CSV to file $path")
    }

    fun isTwitterPath(link: String): Boolean {
        if (isValidUrl(link)) {
            val uri = URI(link)
            return uri.host.endsWith("twitter.com", true)
        }

        if (isHtmlFile(link)) {
            val path = Paths.get(link)
            for (item in path.iterator()) {
                if (item.toString().endsWith("twitter_com", true)) {
                    return true
                }
            }
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
        } catch (ex: java.lang.Exception) {
        }
        return false;
    }
}