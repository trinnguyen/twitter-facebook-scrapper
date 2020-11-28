import models.FbPost
import java.io.File
import java.net.URI
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

    fun writeToFile(pageSource: String, url: String, count: Int): String {

        // write to file
        val path = getPath(url, count)
        val parent = path.toFile().parentFile
        if (!parent.exists()) {
            parent.mkdirs()
        }

        Files.writeString(path, pageSource)
        println("wrote to $path")
        return path.toAbsolutePath().toString()
    }

    fun getCsvFile(file: String): String? {
        val path = Paths.get(file)
        val f = path.toFile()
        if (!f.exists()) return null

        val baseName = f.nameWithoutExtension
        return Paths.get(f.parentFile.absolutePath, "$baseName.csv").toAbsolutePath().toString()
    }

    private fun getPath(url: String, count: Int): Path {
        val uri = URI(url)

        // path: src-gen/facebook_com/apple/
        val filename = "$count.html"
        return Paths.get(
            "src-gen",
            uri.host.replace(".", "_"),
            uri.path.trim('/').replace("/", "_"),
            filename)
    }

    fun writeFbPostToCsv(items: MutableList<FbPost>, csvPath: String) {
        val path = Paths.get(csvPath)
        val builder = StringBuilder()
        builder.append("Title,Date,Time,Reacts,Comments,Shares").append(System.lineSeparator())
        items.forEach {
            builder.append("\"${it.title.replace("\"", "\"\"")}\",${it.formattedDate()},${it.formattedTime()},${it.reacts},${it.comments},${it.shares}").append(System.lineSeparator())
        }

        Files.writeString(path, builder.toString())
        println("wrote to CSV file $path")
    }
}