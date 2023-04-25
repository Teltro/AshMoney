package com.example.ashmoney.models

import com.example.ashmoney.models.ui.AccountUIModel

class Account(
    override var name: String,
    override var amountValue: Double = 0.0,
    var currency: Currency,
    var icon: Icon,
    var iconColor: IconColor
) : AccountUIModel {

    override val activeCurrencyName: String
        get() = currency.name

    override val iconResourceName: String
        get() = icon.resourceName

    override val iconColorValue: String
        get() = iconColor.value

    companion object {
        private var enumerator: Int = 0
    }

    override val id: Int = enumerator++

    fun change(value: Double) {
        amountValue += value;
    }

    override fun equals(other: Any?): Boolean {
        return other is Account &&
                name == other.name &&
                amountValue == other.amountValue &&
                currency == other.currency
    }
}