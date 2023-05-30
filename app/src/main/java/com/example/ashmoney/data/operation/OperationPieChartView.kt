package com.example.ashmoney.data.operation

import androidx.room.ColumnInfo
import com.example.ashmoney.models.ui.OperationListUIModel
import com.example.ashmoney.models.ui.OperationPieChartUIModel
import com.example.ashmoney.models.ui.RecyclerViewUIModel

data class OperationPieChartView(
    @ColumnInfo(name = "id")
    override val id: Int,

    /*@ColumnInfo(name = "name")
    override val name: String?,*/

    @ColumnInfo(name = "operation_type_id")
    override val operationTypeId: Int,

    @ColumnInfo(name = "target_name")
    override val targetName: String,

    @ColumnInfo(name = "target_icon_resource_name")
    override val targetIconResourceName: String,

    @ColumnInfo(name = "target_icon_color_value")
    override val targetIconColorValue: String,

    @ColumnInfo(name = "sum")
    override val sum: Double,

    @ColumnInfo(name = "common_currency_sum")
    override val commonCurrencySum: Double,

    @ColumnInfo(name = "percent")
    override val percent: Double,

    @ColumnInfo(name = "currency_name")
    override val currencyName: String

)  : OperationPieChartUIModel
