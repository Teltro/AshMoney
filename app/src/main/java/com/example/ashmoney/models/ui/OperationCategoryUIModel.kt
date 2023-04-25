package com.example.ashmoney.models.ui

import com.example.ashmoney.models.Icon
import com.example.ashmoney.models.IconColor
import com.example.ashmoney.models.OperationType

interface OperationCategoryUIModel : RecyclerViewUIModel {

    val id: Int
    //val type: OperationType
    val name: String
    val iconResourceName: String
    //var icon: Icon
    val iconColorValue: String
    //var iconColor: IconColor

    override fun same(other: Any?): Boolean {
        return other is OperationCategoryUIModel && this.id == other.id
    }

}