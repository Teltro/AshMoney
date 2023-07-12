package com.example.ashmoney.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.data.dashboardConfig.DashboardAccountingType
import com.example.ashmoney.models.ui.AccountUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface DashboardConfigViewModel {

    interface Inputs {
        fun accountingType(accountingType: DashboardAccountingType) // ??
        fun accountSelectById(accountId: Int) // check list?
        fun accountUnselectById(accountId: Int) // check list?

        /*fun pieChartDataType() // ??
        fun pieChartDataPeriod() // ??

        fun lineChartDataType() // ??
        fun lineChartDataPeriod() // ??*/
    }

    interface Outputs {
        fun accountingType(): StateFlow<DashboardAccountingType> // ??
        fun accountingTypeList(): StateFlow<List<DashboardAccountingType>> // ??
        fun accountList(): StateFlow<List<AccountUIModel>>
        fun accountingAccountList(): StateFlow<List<Int>>

        /*fun pieChartDataType(): StateFlow<Object>// ??
        fun pieChartDataTypeList(): StateFlow<Object> // ??
        fun pieChartDataPeriod(): StateFlow<Object> // ??

        fun lineChartDataType(): StateFlow<Object>// ??
        fun lineChartDataTypeList(): StateFlow<Object> // ??
        fun lineChartDataPeriod(): StateFlow<Object> // ??*/
    }

    class ViewModel : androidx.lifecycle.ViewModel(), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

        private val db
            get() = MainApp.instance.db

        private val dashboardConfigDao = db.dashboardConfigDao()
        private val accountDao = db.accountDao()

        private val accountingType = MutableStateFlow(DashboardAccountingType.BALANCE) // ??
        private val accountingTypeList = MutableStateFlow<List<DashboardAccountingType>>(emptyList())
        private val accountList = MutableStateFlow<List<AccountUIModel>>(emptyList())
        private val accountingAccountList = MutableStateFlow<List<Int>>(mutableListOf())

        init {
            viewModelScope.launch {

                accountingTypeList.value = DashboardAccountingType.values().asList() // TODO to entries

                launch {
                    dashboardConfigDao.getSingleFlow().collect {
                        accountingType.value = it.accountingType
                        accountingAccountList.value = it.accountingAccountsList
                    }
                }

                launch {
                    accountDao.getAllWithAllRelationsFlow().collect {
                        accountList.value = it
                    }
                }

            }
        }

        override fun accountingType(accountingType: DashboardAccountingType) {
            this.accountingType.value = accountingType
        }

        override fun accountSelectById(accountId: Int) {
            this.accountingAccountList.value += accountId
        }

        override fun accountUnselectById(accountId: Int) {
            this.accountingAccountList.value -= accountId
        }


        override fun accountingType(): StateFlow<DashboardAccountingType> = this.accountingType

        override fun accountingTypeList(): StateFlow<List<DashboardAccountingType>> = this.accountingTypeList

        override fun accountList(): StateFlow<List<AccountUIModel>> = this.accountList

        override fun accountingAccountList(): StateFlow<List<Int>> = this.accountingAccountList
    }
}