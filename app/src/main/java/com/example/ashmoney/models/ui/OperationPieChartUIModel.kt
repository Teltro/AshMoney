package com.example.ashmoney.models.ui

interface OperationPieChartUIModel : RecyclerViewUIModel {

    val id: Int
    val name: String?
    val operationTypeId: Int
    val targetName: String
    val targetIconResourceName: String
    val targetIconColorValue: String
    val sum: Double
    val percent: Double
    val currencyName: String

    override fun same(other: Any?): Boolean {
        return other is OperationPieChartUIModel && this.id == other.id
    }
}