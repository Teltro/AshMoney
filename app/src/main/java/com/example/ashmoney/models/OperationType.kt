package com.example.ashmoney.models

import com.example.ashmoney.models.ui.OperationTypeUIModel

class OperationType(
    override val name: String
): OperationTypeUIModel {

    companion object {
        private var enumerator = 1
    }

    override val id: Int = enumerator++

    override fun equals(other: Any?): Boolean {
        return other is OperationType &&
                other.id == id &&
                other.name == name
    }
}