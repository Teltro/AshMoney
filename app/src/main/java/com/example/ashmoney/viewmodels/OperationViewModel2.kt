package com.example.ashmoney.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.data.account.AccountWithAllRelations
import com.example.ashmoney.data.operation.OperationWithAllRelations
import com.example.ashmoney.models.ui.AccountUIModel
import com.example.ashmoney.models.ui.CurrencyUIModel
import com.example.ashmoney.models.ui.OperationCategoryUIModel
import com.example.ashmoney.models.ui.OperationTypeUIModel
import com.example.ashmoney.utils.OperationType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface OperationViewModel2 {

    interface Inputs {
        fun operationType(operationType: OperationTypeUIModel)
        fun fromAccount(fromAccount: AccountUIModel)
        fun toAccount(toAccount: AccountUIModel)
        fun operationCategory(operationCategory: OperationCategoryUIModel)
        fun name(name: String)
        fun currency(currency: CurrencyUIModel)
        fun exchangeRate(exchangeRate: Double)
        fun sum(sum: Double)
        fun note(note: String)

        fun primaryActionClicked()
        fun secondaryActionClicked()

    }

    interface Outputs {
        fun uiState(): StateFlow<UIState>
        fun membersUIState(): StateFlow<MembersUIState>
        fun operationTypeList(): StateFlow<List<OperationTypeUIModel>>
        fun operationType(): StateFlow<OperationTypeUIModel?>
        fun accountList(): StateFlow<List<AccountUIModel>>
        fun fromAccount(): StateFlow<AccountUIModel?>
        fun toAccount(): StateFlow<AccountUIModel?>
        fun operationCategoryList(): StateFlow<List<OperationCategoryUIModel>>
        fun operationCategory(): StateFlow<OperationCategoryUIModel?>
        fun name(): StateFlow<String>
        fun currencyList(): StateFlow<List<CurrencyUIModel>>
        fun currency(): StateFlow<CurrencyUIModel?>
        fun converterState(): StateFlow<ConverterState>
        fun converterFromCurrency(): StateFlow<CurrencyUIModel?>
        fun converterToCurrency(): StateFlow<CurrencyUIModel?>
        fun exchangeRate(): StateFlow<Double>
        fun sum(): StateFlow<Double>
        fun note(): StateFlow<String>
    }

    class ViewModel : androidx.lifecycle.ViewModel(), Inputs, Outputs {

        private val db
            get() = MainApp.instance.db

        private val operationDao = db.operationDao()
        private val operationTypeDao = db.operationTypeDao()
        private val operationCategoryDao = db.operationCategoryDao()
        private val accountDao = db.accountDao()
        private val currencyDao = db.activeCurrencyDao()
        private val currencyExchangeRateDao = db.currencyExchangeRateDao()

        private var state: MutableStateFlow<State> = MutableStateFlow(State.NONE)
        private var currentOperationId: Int? = null
        private var operationEntity: OperationWithAllRelations? = null

        private val uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.none())
        private val operationType: MutableStateFlow<OperationTypeUIModel?> = MutableStateFlow(null)
        private val fromAccount: MutableStateFlow<AccountUIModel?> = MutableStateFlow(null)
        private val toAccount: MutableStateFlow<AccountUIModel?> = MutableStateFlow(null)
        private val operationCategory: MutableStateFlow<OperationCategoryUIModel?> = MutableStateFlow(null)
        private val currency: MutableStateFlow<CurrencyUIModel?> = MutableStateFlow(null)
        private val converterUIState = MutableStateFlow(ConverterState.NONE)
        private val converterFromCurrency: MutableStateFlow<CurrencyUIModel?> = MutableStateFlow(null)
        private val converterToCurrency: MutableStateFlow<CurrencyUIModel?> = MutableStateFlow(null)
        private val currencyExchangeRate: MutableStateFlow<Double> = MutableStateFlow(1.0)
        private val sum: MutableStateFlow<Double> = MutableStateFlow(0.0)
        private val name: MutableStateFlow<String> = MutableStateFlow("")
        private val note: MutableStateFlow<String> = MutableStateFlow("")
        private val converterState: MutableStateFlow<ConverterState> = MutableStateFlow(ConverterState.NONE)

        private val operationTypeList: StateFlow<List<OperationTypeUIModel>> =
            operationTypeDao.getAllFlow()
                .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        private val membersUIState: StateFlow<MembersUIState> = operationType.map { it ->
            val operationType = it?.let { OperationType.fromId(it.id) }
            MembersUIState.fromOperationType(operationType)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, MembersUIState.none())

        private val accountList: StateFlow<List<AccountUIModel>> =
            accountDao.getAllWithAllRelationsFlow()
                .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        private val operationCategoryList: StateFlow<List<OperationCategoryUIModel>> =
            operationCategoryDao.getAllWithAllRelationsFlow()
                .combine(operationType) { operationCategoryList, selectedOperationType ->
                    operationCategoryList.filter { it.operationCategory.operationTypeId == selectedOperationType?.id }
                }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        // TODO find the way to enable collect for selected accounts only for transfer operation type
        private val currencyList: StateFlow<List<CurrencyUIModel>> = combine(
            currencyDao.getAllFlow(),
            operationType,
            fromAccount,
            toAccount
        ) { currencyList, type, fromAccount, toAccount ->
            // transfer
            if (type?.id == OperationType.TRANSFER.id) {
                if (fromAccount != null || toAccount != null) {
                    currencyList.filter {
                        (fromAccount != null) && it.id == (fromAccount as AccountWithAllRelations).activeCurrency.id ||
                                (toAccount != null) && it.id == (toAccount as AccountWithAllRelations).activeCurrency.id
                    }
                } else {
                    emptyList()
                }
            }
            // income or expense
            else {
                currencyList
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        init {
            viewModelScope.launch {
                launch {
                    state.collect {
                        when (it) {
                            State.NONE, State.INIT, State.CREATE -> {
                                cleanOperationData()
                            }

                            State.INFO -> {
                                val result = loadOperationData();
                                if (!result)
                                    state.value = State.NONE
                            }

                            else -> {}
                        }

                        uiState.value = UIState.fromState(it)
                    }
                }

                launch {
                    combine(
                        operationType,
                        fromAccount,
                        toAccount,
                        currencyList, // ??
                        currency
                    ) { operationType, fromAccount, toAccount, currencyList, currency ->

                        var _converterState: ConverterState = ConverterState.WITHOUT_CONVERTER
                        var _currencyExchangeRate: Double? = null


                        // cases with converter
                        if (operationType != null && currencyList.isNotEmpty()) {
                            val operationTypeEnum = OperationType.fromId(operationType.id)
                            if (operationTypeEnum != null) {
                                if (currencyList.size > 1) {
                                    when (operationTypeEnum) {
                                        OperationType.INCOME -> {
                                            if (toAccount != null && currency != null) {
                                                val accountCurrencyId =
                                                    (toAccount as AccountWithAllRelations).activeCurrency.id
                                                if (currency.id != accountCurrencyId) {
                                                    //converterState = ConverterState.CHANGE_TARGET_CURRENCY
                                                    _converterState = ConverterState.WITH_CONVERTER
                                                    _currencyExchangeRate = currencyExchangeRateDao
                                                        .getCurrencyExchangeRateValueByCurrencyId(
                                                            currency.id,
                                                            accountCurrencyId
                                                        )
                                                }
                                            }
                                        }

                                        OperationType.EXPENSE -> {
                                            if (fromAccount != null && currency != null) {
                                                val accountCurrencyId =
                                                    (fromAccount as AccountWithAllRelations).activeCurrency.id
                                                if (currency.id != accountCurrencyId) {
                                                    _converterState = ConverterState.WITH_CONVERTER
                                                    _currencyExchangeRate = currencyExchangeRateDao
                                                        .getCurrencyExchangeRateValueByCurrencyId(
                                                            accountCurrencyId,
                                                            currency.id
                                                        )
                                                }
                                            }
                                        }

                                        OperationType.TRANSFER -> {
                                            if (fromAccount != null && toAccount != null && currency != null) {
                                                val accountFromCurrencyId =
                                                    (fromAccount as AccountWithAllRelations).activeCurrency.id
                                                val accountToCurrencyId =
                                                    (toAccount as AccountWithAllRelations).activeCurrency.id

                                                if (currency.id == accountFromCurrencyId) {
                                                    //converterState = ConverterState.TRANSFER_FROM_CURRENCY
                                                    _converterState =
                                                        ConverterState.WITH_CONVERTER
                                                    _currencyExchangeRate =
                                                        db.currencyExchangeRateDao()
                                                            .getCurrencyExchangeRateValueByCurrencyId(
                                                                accountToCurrencyId,
                                                                accountFromCurrencyId
                                                            )
                                                } else if (currency.id == accountToCurrencyId) {
                                                    //converterState = ConverterState.TRANSFER_TO_CURRENCY
                                                    _converterState =
                                                        ConverterState.WITH_CONVERTER
                                                    _currencyExchangeRate =
                                                        db.currencyExchangeRateDao()
                                                            .getCurrencyExchangeRateValueByCurrencyId(
                                                                accountFromCurrencyId,
                                                                accountToCurrencyId
                                                            )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        converterUIState.value = _converterState
                        currencyExchangeRate.value = _currencyExchangeRate ?: 1.0

                    }.collect()
                }
            }
        }

        fun start(operationId: Int?) {
            if (currentOperationId != operationId) {
                currentOperationId = operationId
                state.run {
                    value = State.INIT
                    viewModelScope.launch {
                        when {
                            operationId == null -> value = State.NONE
                            operationId == -1 -> value = State.CREATE
                            operationId >= 0 -> value = State.INFO
                        }
                    }
                }
            }
        }

        private fun cleanOperationData() {
            operationType.value = null
            fromAccount.value = null
            toAccount.value = null
            operationCategory.value = null
            currency.value = null
            name.value = ""
            currencyExchangeRate.value = 1.0
            sum.value = 0.0
            note.value = ""
        }

        private suspend fun loadOperationData(): Boolean {
            currentOperationId?.let { id ->
                operationEntity = operationDao.getWithAllRelationsById(id)
                operationEntity
            }?.let { operationData ->
                operationType.value = operationData.operationType
                fromAccount.value = operationData.fromAccount
                toAccount.value = operationData.toAccount
                operationCategory.value = operationData.operationCategory
                currency.value = operationData.activeCurrency
                name.value = operationData.operation.name ?: ""
                exchangeRate.value = operationData.operation.exchangeRateCoefficient
                sum.value = operationData.operation.sum
                note.value = ""
                return true
            } ?: return false
        }

        // INPUTS
        override fun operationType(operationType: OperationTypeUIModel) {
            TODO("Not yet implemented")
        }

        override fun fromAccount(fromAccount: AccountUIModel) {
            TODO("Not yet implemented")
        }

        override fun toAccount(toAccount: AccountUIModel) {
            TODO("Not yet implemented")
        }

        override fun operationCategory(operationCategory: OperationCategoryUIModel) {
            TODO("Not yet implemented")
        }

        override fun name(name: String) {
            TODO("Not yet implemented")
        }

        override fun currency(currency: CurrencyUIModel) {
            TODO("Not yet implemented")
        }

        override fun exchangeRate(exchangeRate: Double) {
            TODO("Not yet implemented")
        }

        override fun sum(sum: Double) {
            TODO("Not yet implemented")
        }

        override fun note(note: String) {
            TODO("Not yet implemented")
        }

        override fun primaryActionClicked() {
            TODO("Not yet implemented")
        }

        override fun secondaryActionClicked() {
            TODO("Not yet implemented")
        }

        // OUTPUTS
        override fun operationTypeList(): StateFlow<List<OperationTypeUIModel>> = operationTypeList
        override fun operationType(): StateFlow<OperationTypeUIModel?> = operationType
        override fun accountList(): StateFlow<List<AccountUIModel>> = accountList
        override fun fromAccount(): StateFlow<AccountUIModel?> = fromAccount
        override fun toAccount(): StateFlow<AccountUIModel?> = toAccount
        override fun operationCategoryList(): StateFlow<List<OperationCategoryUIModel>> =
            operationCategoryList

        override fun operationCategory(): StateFlow<OperationCategoryUIModel?> = operationCategory
        override fun name(): StateFlow<String> = name
        override fun currencyList(): StateFlow<List<CurrencyUIModel>> = currencyList
        override fun currency(): StateFlow<CurrencyUIModel?> = currency
        override fun note(): StateFlow<String> = note
        override fun converterState(): StateFlow<ConverterState> = converterState
        override fun converterFromCurrency(): StateFlow<CurrencyUIModel?> = converterFromCurrency
        override fun converterToCurrency(): StateFlow<CurrencyUIModel?> = converterToCurrency
        override fun exchangeRate(): StateFlow<Double> = exchangeRate
        override fun sum(): StateFlow<Double> = sum
    }

    data class UIState(
        val enableChange: Boolean,
        val primaryActionButtonText: String,
        val secondaryActionButtonText: String,
    ) {
        companion object {
            fun info(): UIState =
                UIState(
                    false,
                    "Изменить",
                    "Удалить"
                )

            fun create(): UIState =
                UIState(
                    true,
                    "Создать",
                    "Отменить"
                )

            fun none(): UIState =
                UIState(
                    false,
                    "",
                    ""
                )

            fun fromState(state: State) =
                when (state) {
                    State.NONE, State.INIT -> none()
                    State.INFO -> info()
                    State.CREATE -> create()
                }
        }
    }

    data class MembersUIState(
        val fromAccountVisible: Boolean,
        val toAccountVisible: Boolean,
        val operationCategoryVisible: Boolean
    ) {
        companion object {
            fun income() = MembersUIState(
                fromAccountVisible = false,
                toAccountVisible = true,
                operationCategoryVisible = true
            )

            fun expense() = MembersUIState(
                fromAccountVisible = true,
                toAccountVisible = false,
                operationCategoryVisible = true
            )

            fun transfer() = MembersUIState(
                fromAccountVisible = true,
                toAccountVisible = true,
                operationCategoryVisible = false
            )

            fun none() = MembersUIState(
                fromAccountVisible = false,
                toAccountVisible = false,
                operationCategoryVisible = false
            )

            fun fromOperationType(operationType: OperationType?) = when (operationType) {
                OperationType.INCOME -> income()
                OperationType.EXPENSE -> expense()
                OperationType.TRANSFER -> transfer()
                null -> none()
            }
        }
    }

    enum class State {
        NONE,
        INIT,
        CREATE,
        INFO
    }

    enum class ConverterState {
        NONE,
        WITHOUT_CONVERTER,
        WITH_CONVERTER,
    }
}