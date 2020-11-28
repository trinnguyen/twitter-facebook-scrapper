package models

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class FbPost(val title: String, val epochTime: Long, val reacts: Int, val comments: Int, val shares: Int) {
    fun formattedTime(): String {
        val instant = Instant.ofEpochSecond(epochTime)
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toString()
    }
}