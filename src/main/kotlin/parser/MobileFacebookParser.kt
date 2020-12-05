package parser

import models.CsvRow

class MobileFacebookParser: PageParser() {

    override fun parseToRows(source: String): List<CsvRow> {
        return emptyList()
    }

}