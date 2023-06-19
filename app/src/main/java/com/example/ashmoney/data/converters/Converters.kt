package com.example.ashmoney.data.converters

import androidx.room.TypeConverter
import com.example.ashmoney.utils.fromIsoToDate
import com.example.ashmoney.utils.fromIsoToLocalDateTime
import com.example.ashmoney.utils.toIsoString
import java.time.LocalDateTime
import java.util.Date

class Converters {
    @TypeConverter
    fun fromDateTimeString(value: String?): LocalDateTime? {
        return value?.fromIsoToLocalDateTime()
    }

    @TypeConverter
    fun toDateTimeString(value: LocalDateTime?): String? {
        return value?.toIsoString()
    }
}
