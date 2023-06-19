package com.example.ashmoney.viewmodels

import com.example.ashmoney.models.ui.AccountUIModel
import kotlinx.coroutines.flow.StateFlow

interface DashboardConfigViewModel {

    interface Inputs {
        fun accountsSumType() // ??
        fun accountSelection(account: AccountUIModel) // check list?

        fun pieChartDataType() // ??
        fun pieChartDataPeriod() // ??

        fun lineChartDataType() // ??
        fun lineChartDataPeriod() // ??
    }

    interface Outputs {
        fun accountsSumType() // ??
        fun accountsSumTypeList(): StateFlow<Object> // ??
        fun accountList(): StateFlow<AccountUIModel>

        fun pieChartDataType(): StateFlow<Object>// ??
        fun pieChartDataTypeList(): StateFlow<Object> // ??
        fun pieChartDataPeriod(): StateFlow<Object> // ??

        fun lineChartDataType(): StateFlow<Object>// ??
        fun lineChartDataTypeList(): StateFlow<Object> // ??
        fun lineChartDataPeriod(): StateFlow<Object> // ??
    }

    class ViewModel : androidx.lifecycle.ViewModel(), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

    }
}