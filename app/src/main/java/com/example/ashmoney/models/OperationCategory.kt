package com.example.ashmoney.models

import com.example.ashmoney.models.ui.OperationCategoryUIModel
import com.example.ashmoney.models.ui.OperationTypeUIModel

class OperationCategory(
    override var name: String,
    var type: OperationType,
    var icon: Icon,
    var iconColor: IconColor
) : OperationCategoryUIModel {

    override val iconResourceName: String
        get() = icon.resourceName

    override val iconColorValue: String
        get() = iconColor.value

    companion object {
        private var enumerator = 1
    }

    override val id: Int = enumerator++

    override fun equals(other: Any?): Boolean {
        return other is OperationCategory &&
                other.id == id &&
                other.name == name &&
                other.type == type
    }
}