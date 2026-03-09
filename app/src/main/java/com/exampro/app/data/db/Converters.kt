package com.exampro.app.data.db

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converters {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let {
            try {
                dateFormat.parse(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        return date?.let { dateFormat.format(it) }
    }
}
