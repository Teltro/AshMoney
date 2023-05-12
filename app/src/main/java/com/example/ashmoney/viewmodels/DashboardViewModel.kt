package com.example.ashmoney.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.data.operation.OperationLineChartView
import com.example.ashmoney.data.operation.OperationPieChartView
import com.example.ashmoney.models.ui.CurrencyUIModel
import com.example.ashmoney.models.ui.OperationLineChartUIModel
import com.example.ashmoney.models.ui.OperationPieChartUIModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

interface DashboardViewModel {

    interface Inputs {
        fun plusClick()
    }

    interface Outputs {
        fun toAddOperationPage(): SharedFlow<Unit>
        fun totalSum(): StateFlow<Double>
        fun globalCurrency(): StateFlow<CurrencyUIModel?>
        fun pieChartOperationList(): StateFlow<List<OperationPieChartUIModel>>
        fun lineChartOperationList(): StateFlow<List<OperationLineChartUIModel>>
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
        private val lineChartOperationList = MutableStateFlow<List<OperationLineChartView>>(emptyList())
        private val toAddOperationPage = MutableSharedFlow<Unit>()

        init {
            viewModelScope.launch {
                launch {
                    accountDao.getTotalSumFlow(defaultCurrencyId).collect { totalSum.value = it }
                }
                globalCurrency.value = currencyDao.getById(defaultCurrencyId)

                launch {
                    operationDao.getPieChartViewFlowByOperationTypeId(2, defaultCurrencyId).collect {
                        pieChartOperationList.value = it
                    }
                }

                launch {
                    operationDao.getLineChartViewFlowByOperationTypeId(1).collect {
                        lineChartOperationList.value = it
                    }
                }
            }
        }

        override fun plusClick() {
            viewModelScope.launch {
                toAddOperationPage.emit(Unit)
            }
        }

        override fun toAddOperationPage() = toAddOperationPage
        override fun totalSum(): StateFlow<Double> = totalSum
        override fun globalCurrency(): StateFlow<CurrencyUIModel?> = globalCurrency
        override fun pieChartOperationList(): StateFlow<List<OperationPieChartView>> = pieChartOperationList
        override fun lineChartOperationList(): StateFlow<List<OperationLineChartUIModel>> = lineChartOperationList
    }

}