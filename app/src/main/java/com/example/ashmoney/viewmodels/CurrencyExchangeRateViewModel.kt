package com.example.ashmoney.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.ui.CurrencyExchangeRateUIModel
import com.example.ashmoney.models.ui.CurrencyUIModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface CurrencyExchangeRateViewModel {


    interface Inputs {
        fun currency(currency: CurrencyUIModel?)
        fun convertingSum(convertingSum: Double)
    }

    interface Outputs {
        fun currency(): StateFlow<CurrencyUIModel?>
        fun currencyList(): StateFlow<List<CurrencyUIModel>>
        fun convertingSum(): StateFlow<Double>
        fun currencyExchangeRateList(): StateFlow<List<CurrencyExchangeRateUIModel>>
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    class ViewModel : androidx.lifecycle.ViewModel(), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

        private val activeCurrencyDao = MainApp.instance.db.activeCurrencyDao()
        private val currencyExchangeRateDao = MainApp.instance.db.currencyExchangeRateDao()

        // must be in the user settings as default currency
        private val defaultCurrencyId = 2

        private val currency = MutableStateFlow<CurrencyUIModel?>(null)
        private val currencyList = MutableStateFlow<List<CurrencyUIModel>>(emptyList())
        private val convertingSum = MutableStateFlow(1.0)
        private val currencyExchangeRateList = MutableStateFlow<List<CurrencyExchangeRateUIModel>>(emptyList())

        init {
            viewModelScope.launch {
                launch {
                    activeCurrencyDao.getAllFlow().collect { currencyList.value = it }
                }
                launch {
                    currency.flatMapLatest {
                        it?.let {
                            currencyExchangeRateDao.getAllEntityViewFlowByCurrencyId(it.id)
                        } ?: emptyFlow()
                    }.collect { currencyExchangeRateList.value = it }
                }
            }
        }

        fun start() {
            viewModelScope.launch {
                activeCurrencyDao.getById(defaultCurrencyId).let {
                    currency.value = it
                }
            }
        }

        override fun currency(currency: CurrencyUIModel?) {
            this.currency.value = currency
        }

        override fun convertingSum(convertingSum: Double) {
            this.convertingSum.value = convertingSum
        }

        override fun currency(): StateFlow<CurrencyUIModel?> = currency
        override fun currencyList(): StateFlow<List<CurrencyUIModel>> = currencyList
        override fun convertingSum(): StateFlow<Double> = convertingSum
        override fun currencyExchangeRateList(): StateFlow<List<CurrencyExchangeRateUIModel>> = currencyExchangeRateList
    }

}