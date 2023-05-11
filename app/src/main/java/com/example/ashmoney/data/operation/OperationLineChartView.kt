package com.example.ashmoney.data.operation

import androidx.room.ColumnInfo
import com.example.ashmoney.models.ui.OperationLineChartUIModel

data class OperationLineChartView(
    @ColumnInfo(name = "id")
    override val id: Int,
    @ColumnInfo(name = "sum")
    override val sum: Double,
    @ColumnInfo(name = "date_time")
    override val dateTime: String,
) : OperationLineChartUIModel


