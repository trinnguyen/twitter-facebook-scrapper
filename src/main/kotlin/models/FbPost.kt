package models

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class FbPost(id: String, val title: String, epochTime: Long, val reacts: Int, val comments: Int, val shares: Int): CsvRow(id, Instant.ofEpochSecond(epochTime)) {
    private fun formattedDate(): String {
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return date.toLocalDate().toString()
    }

    private fun formattedTime(): String {
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dtf = DateTimeFormatter.ofPattern("HH:mm")
        return dtf.format(date.toLocalTime())
    }

    override fun header(): String {
        return "Title,Date,Time,Reacts,Comments,Shares,Id"
    }

    override fun formatCsv(): String {
        return "\"${title.replace("\"", "\"\"")}\",${formattedDate()},${formattedTime()},${reacts},${comments},${shares},$id"
    }
}