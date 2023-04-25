package com.example.ashmoney.models.ui

interface OperationTypeUIModel: RecyclerViewUIModel {

    val id: Int
    val name: String

    override fun same(other: Any?): Boolean {
        return other is OperationTypeUIModel && this.id == other.id
    }

}