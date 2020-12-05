package parser

import models.CsvRow
import java.nio.file.Files
import java.nio.file.Paths

abstract class PageParser {

    fun parseToCsv(path: String): String? {
        val csvPath = Util.getCsvFile(path) ?: return null
        if (parseToCsv(Files.readString(Paths.get(path)), csvPath)) {
            return csvPath
        }

        return null
    }

    fun parseToCsv(source: String, csvPath: String): Boolean {
        val list = parseToRows(source)
        Util.writeCsvRows(list, csvPath)
        println("\t found ${list.size} items")
        if (list.isNotEmpty()) {
            println("\t latest: ${list.last().formatCsv()}")
        }
        return true
    }

    abstract fun parseToRows(source: String): List<CsvRow>
}