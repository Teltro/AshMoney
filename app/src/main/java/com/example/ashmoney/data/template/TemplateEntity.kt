package com.example.ashmoney.data.template

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "template")
data class TemplateEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "operation_type_id")
    val operationTypeId: Int?,

    @ColumnInfo(name = "from_account_id")
    val fromAccountId: Int?,

    @ColumnInfo(name = "to_account_id")
    val toAccountId: Int?,

    @ColumnInfo(name = "operation_category_id")
    val operationCategoryId: Int?,

    @ColumnInfo(name = "sum")
    val sum: Double?,

    @ColumnInfo(name = "currency_exchange_rate")
    val exchangeRateCoefficient: Double?,

    @ColumnInfo(name = "active_currency_id")
    val activeCurrencyId: Int?,

    @ColumnInfo(name = "note")
    val note: String?
)
