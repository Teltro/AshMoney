package com.example.ashmoney.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.data.operation.OperationPieChartView
import com.example.ashmoney.models.ui.CurrencyUIModel
import com.example.ashmoney.models.ui.OperationListUIModel
import com.example.ashmoney.models.ui.OperationUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface DashboardViewModel {

    interface Inputs {
    }

    interface Outputs {
        fun totalSum(): StateFlow<Double>
        fun globalCurrency(): StateFlow<CurrencyUIModel?>
        fun pieChartOperationList(): StateFlow<List<OperationPieChartView>>
    }

    class ViewModel : androidx.lifecycle.ViewModel(), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

        private val db
            get() = MainApp.instance.db

        private val accountDao = db.accountDao()
        private val currencyDao = db.activeCurrencyDao()
        private val operationDao = db.operationDao()

        // must be in the user settings as default currency
        private val defaultCurrencyId = 2

        private val totalSum = MutableStateFlow(0.0)
        private val globalCurrency = MutableStateFlow<CurrencyUIModel?>(null)
        private val pieChartOperationList = MutableStateFlow<List<OperationPieChartView>>(emptyList())

        init {
            viewModelScope.launch {
                launch {
                    accountDao.getTotalSumFlow(defaultCurrencyId).collect { totalSum.value = it }
                }
                globalCurrency.value = currencyDao.getById(defaultCurrencyId)

                launch {
                    operationDao.getAllViewEntityFlowByOperationTypeId(2).collect {
                        pieChartOperationList.value = it
                    }
                }
            }
        }

        override fun totalSum(): StateFlow<Double> = totalSum
        override fun globalCurrency(): StateFlow<CurrencyUIModel?> = globalCurrency
        override fun pieChartOperationList(): StateFlow<List<OperationPieChartView>> = pieChartOperationList
    }


    data class Data(
        var totalSum: Double? = null,
        var globalCurrency: CurrencyUIModel? = null,
    )

}