package models

import java.time.Instant

abstract class CsvRow (val id: String, val instant: Instant) {

    abstract fun header(): String

    abstract fun  formatCsv(): String
}