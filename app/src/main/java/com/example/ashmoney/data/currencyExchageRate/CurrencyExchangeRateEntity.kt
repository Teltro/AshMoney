package com.example.ashmoney.data.currencyExchageRate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ashmoney.data.converters.Converters
import java.time.LocalDateTime
import java.util.Date

@Entity(
    tableName = "currency_exchange_rate",
    indices = [Index(value = ["currency_from_id", "currency_to_id"], unique = true)]
)
data class CurrencyExchangeRateEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "currency_from_id")
    val currencyFromId: Int,

    @ColumnInfo(name = "currency_to_id")
    val currencyToId: Int,

    @ColumnInfo(name = "exchange_rate")
    val exchangeRate: Double,

    @ColumnInfo(name = "date_time")
    @TypeConverters(Converters::class)
    val dateTime: LocalDateTime
)
