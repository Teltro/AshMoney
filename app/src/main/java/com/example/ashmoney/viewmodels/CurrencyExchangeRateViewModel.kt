package com.example.ashmoney.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.ui.CurrencyExchangeRateUIModel
import com.example.ashmoney.models.ui.CurrencyUIModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CurrencyExchangeRateViewModel : ViewModel() {

    // must be in the user settings as default currency
    private val defaultCurrencyId = 1

    private val db
        get() = MainApp.instance.db

    val data = Data();

    val state: MutableStateFlow<State> = MutableStateFlow(State.NONE)

    val currencyList: StateFlow<List<CurrencyUIModel>> =
        db.activeCurrencyDao().getAllFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val selectedCurrency: MutableStateFlow<CurrencyUIModel?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currencyExchangeRateList: StateFlow<List<CurrencyExchangeRateUIModel>> =
        selectedCurrency.flatMapLatest {
            it?.let {
                db.currencyExchangeRateDao().getAllEntityViewFlowByCurrencyId(it.id)
            } ?: emptyFlow()
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    fun start() {
        state.value = State.INIT
        viewModelScope.launch {
            db.activeCurrencyDao().getById(defaultCurrencyId).let {
                selectedCurrency.value = it
                data.selectedCurrency = it
            }
            data.sum = 1.0

            state.value = State.INFO
        }
    }

    data class Data(
        var selectedCurrency: CurrencyUIModel? = null,
        var sum: Double? = null,
    )

    enum class State {
        NONE,
        INIT,
        INFO
    }
}