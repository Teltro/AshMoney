package com.example.ashmoney.models.ui

interface CurrencyUIModel : UIModel, RecyclerViewUIModel {

    val id: Int
    val name: String

    override fun same(other: Any?): Boolean {
        return other is CurrencyUIModel && this.id == other.id
    }

}