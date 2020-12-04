package models

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


data class FbPost(val title: String, val epochTime: Long, val reacts: Int, val comments: Int, val shares: Int): CsvRow {
    fun formattedDate(): String {
        val instant = Instant.ofEpochSecond(epochTime)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return date.toLocalDate().toString()
    }

    fun formattedTime(): String {
        val instant = Instant.ofEpochSecond(epochTime)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dtf = DateTimeFormatter.ofPattern("HH:mm")
        return dtf.format(date.toLocalTime())
    }

    override fun header(): String {
        return "Title,Date,Time,Reacts,Comments,Shares"
    }

    override fun formatCsv(): String {
        return "\"${title.replace("\"", "\"\"")}\",${formattedDate()},${formattedTime()},${reacts},${comments},${shares}"
    }
}