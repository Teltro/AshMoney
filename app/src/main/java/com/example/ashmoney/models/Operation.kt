package com.example.ashmoney.models

import com.example.ashmoney.models.ui.OperationUIModel

class Operation(
    override val name: String?,
    override val accountTo: Account?,
    override val accountFrom: Account?,
    override val sum: Double,
    override val currency: Currency,
    override val type: OperationType,
    override val category: OperationCategory?,
    override val note: String?
) : OperationUIModel {

    companion object {
        private var enumerator = 0
    }

    override val id: Int = enumerator++

    override fun equals(other: Any?): Boolean {
        return other is Operation &&
                name == other.name &&
                accountTo == other.accountTo &&
                accountFrom == other.accountFrom &&
                sum == other.sum &&
                currency == other.currency &&
                type == other.type &&
                category == other.category &&
                note == other.note
    }

}