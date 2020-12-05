package models

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Tweet(val id: String, val text: String, val instant: Instant, val replies: String, val retweets: String, val likes: String): CsvRow {

    override fun header(): String {
        return "Link,Text,Date,Time,Replies,Retweets,Likes"
    }

    private fun formattedDate(): String {
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return date.toLocalDate().toString()
    }

    private fun formattedTime(): String {
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dtf = DateTimeFormatter.ofPattern("HH:mm")
        return dtf.format(date.toLocalTime())
    }

    override fun formatCsv(): String {
        return "${id},\"${text.replace("\"", "\"\"")}\",${formattedDate()},${formattedTime()},${replies},${retweets},${likes}"
    }
}