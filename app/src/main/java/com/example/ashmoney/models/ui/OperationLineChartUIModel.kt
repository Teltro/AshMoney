package com.example.ashmoney.models.ui

import java.time.LocalDateTime
import java.util.Date

interface OperationLineChartUIModel {
    val id: Int
    val sum: Double
    val dateTime: LocalDateTime
}