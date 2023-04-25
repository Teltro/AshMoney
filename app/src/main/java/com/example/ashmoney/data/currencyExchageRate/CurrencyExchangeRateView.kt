package com.example.ashmoney.data.currencyExchageRate

import androidx.room.ColumnInfo
import com.example.ashmoney.models.ui.CurrencyExchangeRateUIModel
import com.example.ashmoney.models.ui.CurrencyUIModel

data class CurrencyExchangeRateView(

    @ColumnInfo(name = "id")
    override val id: Int,

    @ColumnInfo(name = "currency_name")
    override val currencyName: String,

    @ColumnInfo(name = "rate")
    override val rate: Double,

) : CurrencyExchangeRateUIModel
