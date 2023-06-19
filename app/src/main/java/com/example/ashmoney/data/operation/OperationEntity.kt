package com.example.ashmoney.data.operation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ashmoney.data.converters.Converters
import java.time.LocalDateTime
import java.util.Date

@Entity(tableName = "operation")
data class OperationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "period_id")
    val periodId: Int? = null,

    @ColumnInfo(name = "operation_type_id")
    val operationTypeId: Int,

    @ColumnInfo(name = "from_account_id")
    val fromAccountId: Int?,

    @ColumnInfo(name = "to_account_id")
    val toAccountId: Int?,

    @ColumnInfo(name = "operation_category_id")
    val operationCategoryId: Int?,

    @ColumnInfo(name = "sum")
    val sum: Double,

    @ColumnInfo(name = "currency_exchange_rate", defaultValue = "1.0")
    val exchangeRateCoefficient: Double = 1.0,

    @ColumnInfo(name = "active_currency_id")
    val activeCurrencyId: Int,

    @ColumnInfo(name = "date_time")
    @TypeConverters(Converters::class)
    val dateTime: LocalDateTime,

    @ColumnInfo(name = "note")
    val note: String?


)
