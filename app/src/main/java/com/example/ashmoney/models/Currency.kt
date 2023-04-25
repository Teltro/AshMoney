package com.example.ashmoney.models

import com.example.ashmoney.models.ui.CurrencyUIModel

class Currency(
    override val name: String,
    val isoCode: String,
    ) : CurrencyUIModel {

    companion object{
        private var enumerator = 1;
    }

    override val id: Int = enumerator++;

    override fun equals(other: Any?): Boolean = other is Currency && name == other.name
}