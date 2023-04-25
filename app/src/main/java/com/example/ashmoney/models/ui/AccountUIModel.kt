package com.example.ashmoney.models.ui

import com.example.ashmoney.models.Currency
import com.example.ashmoney.models.Icon
import com.example.ashmoney.models.IconColor

interface AccountUIModel : RecyclerViewUIModel {

    val id: Int
    val name: String
    val amountValue: Double
    val activeCurrencyName: String
    val iconResourceName: String
    val iconColorValue: String

    override fun same(other: Any?): Boolean {
        return other is AccountUIModel && this.id == other.id
    }

}