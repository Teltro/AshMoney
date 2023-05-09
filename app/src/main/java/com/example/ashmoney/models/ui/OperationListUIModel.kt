package com.example.ashmoney.models.ui

interface OperationListUIModel : RecyclerViewUIModel {

    val id: Int
    val name: String?
    val operationTypeId: Int
    val fromName: String
    val fromIconResourceName: String
    val fromIconColorValue: String
    val toName: String
    val toIconResourceName: String
    val toIconColorValue: String
    val sum: Double
    val currencyName: String

    override fun same(other: Any?): Boolean {
        return other is OperationUIModel && this.id == other.id
    }

}