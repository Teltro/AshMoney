package com.example.ashmoney.models.ui

interface CurrencyExchangeRateUIModel: RecyclerViewUIModel {

    val id: Int
    val currencyName: String
    val rate: Double

    override fun same(other: Any?): Boolean {
        return other is CurrencyExchangeRateUIModel && this.id == other.id
    }
}