package com.example.ashmoney.data.activeCurrency

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ashmoney.models.ui.CurrencyUIModel

@Entity(tableName = "active_currency")
data class ActiveCurrencyEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Int = 0,

    @ColumnInfo(name = "name")
    override val name: String,

    @ColumnInfo(name = "iso_code")
    val isoCode: Int,

) : CurrencyUIModel