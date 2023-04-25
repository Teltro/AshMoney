package com.example.ashmoney.models.ui

import com.example.ashmoney.models.Account
import com.example.ashmoney.models.Currency
import com.example.ashmoney.models.OperationCategory
import com.example.ashmoney.models.OperationType

interface OperationUIModel : RecyclerViewUIModel {

    val id: Int
    val name: String?
    val accountTo: Account?
    val accountFrom: Account?
    val sum: Double
    val currency: Currency
    val type: OperationType
    val category: OperationCategory?
    val note: String?


    override fun same(other: Any?): Boolean {
        return other is OperationUIModel && this.id == other.id
    }

}