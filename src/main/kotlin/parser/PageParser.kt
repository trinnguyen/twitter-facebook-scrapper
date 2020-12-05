package parser

import ULogger.logInfo
import models.CsvRow
import java.nio.file.Files
import java.nio.file.Paths

abstract class PageParser {

    fun parseHtmlFileToCsv(path: String): String? {
        logInfo("start parsing HTML $path")
        val csvPath = Util.generatePathFromFile(path, "csv") ?: return null
        if (parseHtmlFileToCsv(Files.readString(Paths.get(path)), csvPath)) {
            return csvPath
        }

        return null
    }

    fun parseHtmlFileToCsv(source: String, csvPath: String): Boolean {
        val list = parseToRows(source)
        Util.writeCsvRows(list, csvPath)
        logInfo("\t found ${list.size} items")
        if (list.isNotEmpty()) {
            logInfo("\t latest: ${list.last().formatCsv()}")
        }
        return true
    }

    abstract fun parseToRows(source: String): List<CsvRow>
}