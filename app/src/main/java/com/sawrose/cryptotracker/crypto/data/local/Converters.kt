package com.sawrose.cryptotracker.crypto.data.local

import androidx.room.TypeConverter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    @TypeConverter
    fun fromTimestamp(value: String?): ZonedDateTime? {
        return value?.let {
            ZonedDateTime.parse(it, formatter)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: ZonedDateTime?): String? {
        return date?.format(formatter)
    }
}
