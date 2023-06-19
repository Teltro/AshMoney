package com.example.ashmoney.data.operation

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import com.example.ashmoney.data.converters.Converters
import com.example.ashmoney.models.ui.OperationLineChartUIModel
import java.time.LocalDateTime
import java.util.Date

data class OperationLineChartView(
    @ColumnInfo(name = "id")
    override val id: Int,
    @ColumnInfo(name = "sum")
    override val sum: Double,
    @ColumnInfo(name = "date_time")
    @TypeConverters(Converters::class)
    override val dateTime: LocalDateTime,
) : OperationLineChartUIModel


