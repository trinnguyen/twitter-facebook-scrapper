package models

interface CsvRow {
    fun header(): String

    fun formatCsv(): String
}