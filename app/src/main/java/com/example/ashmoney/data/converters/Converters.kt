package com.example.ashmoney.data.converters

import androidx.room.TypeConverter
import com.example.ashmoney.utils.fromIsoToDate
import com.example.ashmoney.utils.toIsoString
import java.util.Date

class Converters {
    @TypeConverter
    fun fromDateTimeString(value: String?): Date? {
        return value?.fromIsoToDate()
    }

    @TypeConverter
    fun toDateTimeString(value: Date?): String? {
        return value?.toIsoString()
    }
}
