package com.exampro.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    private val displayDateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US)

    fun parseIsoDate(dateString: String?): Date? {
        if (dateString.isNullOrBlank()) return null
        return try {
            isoFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDate(date: Date?): String {
        if (date == null) return ""
        return displayDateFormat.format(date)
    }

    fun formatDateTime(date: Date?): String {
        if (date == null) return ""
        return displayDateTimeFormat.format(date)
    }

    fun formatTimestamp(timestamp: Long): String {
        return displayDateTimeFormat.format(Date(timestamp))
    }

    fun Date.toDisplayDate(): String = formatDate(this)

    fun Date.toDisplayDateTime(): String = formatDateTime(this)

    fun String.toDate(): Date? = parseIsoDate(this)

    fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, remainingSeconds)
    }
}
