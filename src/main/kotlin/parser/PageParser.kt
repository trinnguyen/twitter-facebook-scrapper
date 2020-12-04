package parser

import java.nio.file.Files
import java.nio.file.Paths

interface PageParser {

    fun parseToCsv(path: String): String? {
        val csvPath = Util.getCsvFile(path) ?: return null
        if (parseToCsv(Files.readString(Paths.get(path)), csvPath)) {
            return csvPath
        }

        return null
    }

    fun parseToCsv(source: String, csvPath: String): Boolean
}