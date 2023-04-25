package com.example.ashmoney.models.ui

interface IconUIModel : UIModel, RecyclerViewUIModel {
    val id: Int?
    val name: String
    val resourceName: String

    override fun same(other: Any?): Boolean {
        return other is IconUIModel && this.id == other.id
    }

}