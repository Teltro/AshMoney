package com.example.ashmoney.models.ui

interface IconColorUIModel: RecyclerViewUIModel {

    val id: Int

    val value: String

    override fun same(other: Any?): Boolean {
        return other is IconColorUIModel && this.id == other.id
    }

    override fun equals(other: Any?): Boolean
}