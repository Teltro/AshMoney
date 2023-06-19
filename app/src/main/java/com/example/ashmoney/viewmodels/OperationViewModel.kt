package com.example.ashmoney.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.data.account.AccountWithAllRelations
import com.example.ashmoney.data.operation.OperationEntity
import com.example.ashmoney.data.operation.OperationWithAllRelations
import com.example.ashmoney.models.ui.AccountUIModel
import com.example.ashmoney.models.ui.CurrencyUIModel
import com.example.ashmoney.models.ui.OperationCategoryUIModel
import com.example.ashmoney.models.ui.OperationTypeUIModel
import com.example.ashmoney.utils.OperationTypeId
import com.example.ashmoney.utils.toIsoString
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

interface OperationViewModel {

    interface Inputs {
        fun operationType(operationType: OperationTypeUIModel?)
        fun fromAccount(fromAccount: AccountUIModel?)
        fun toAccount(toAccount: AccountUIModel?)
        fun operationCategory(operationCategory: OperationCategoryUIModel?)
        fun name(name: String)
        fun currency(currency: CurrencyUIModel?)
        fun currencyExchangeRate(currencyExchangeRate: Double)
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
        fun converterUIState(): StateFlow<ConverterState>
        fun converterFromCurrency(): StateFlow<String>
        fun converterToCurrency(): StateFlow<String>
        fun currencyExchangeRate(): StateFlow<Double>
        fun sum(): StateFlow<Double>
        fun converteredSum(): StateFlow<Double>
        fun note(): StateFlow<String>
        fun leavePage(): SharedFlow<Unit>
    }

    class ViewModel : androidx.lifecycle.ViewModel(), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

        private val db
            get() = MainApp.instance.db

        private val operationDao = db.operationDao()
        private val operationTypeDao = db.operationTypeDao()
        private val operationCategoryDao = db.operationCategoryDao()
        private val accountDao = db.accountDao()
        private val currencyDao = db.activeCurrencyDao()
        private val currencyExchangeRateDao = db.currencyExchangeRateDao()

        private var state = MutableStateFlow(State.NONE)
        private var currentOperationId: Int? = null
        private var operationEntity: OperationWithAllRelations? = null

        private val uiState = MutableStateFlow(UIState.none())
        private val operationType = MutableStateFlow<OperationTypeUIModel?>(null)
        private val operationTypeList = MutableStateFlow<List<OperationTypeUIModel>>(emptyList())
        private val membersUIState = MutableStateFlow(MembersUIState.none())
        private val fromAccount = MutableStateFlow<AccountUIModel?>(null)
        private val toAccount = MutableStateFlow<AccountUIModel?>(null)
        private val accountList = MutableStateFlow<List<AccountUIModel>>(emptyList())
        private val operationCategory = MutableStateFlow<OperationCategoryUIModel?>(null)
        private val operationCategoryList =
            MutableStateFlow<List<OperationCategoryUIModel>>(emptyList())
        private val currency = MutableStateFlow<CurrencyUIModel?>(null)
        private val currencyList = MutableStateFlow<List<CurrencyUIModel>>(emptyList())
        private val converterUIState = MutableStateFlow(ConverterState.NONE)
        private val converterFromCurrency = MutableStateFlow<String>("")
        private val converterToCurrency = MutableStateFlow<String>("")
        private val currencyExchangeRate = MutableStateFlow(1.0)
        private val sum = MutableStateFlow(0.0)
        private val converteredSum = MutableStateFlow(0.0)
        private val name = MutableStateFlow("")
        private val note = MutableStateFlow("")
        private val leavePage = MutableSharedFlow<Unit>()


        init {
            viewModelScope.launch {
                launch {
                    operationTypeDao.getAllFlow().collect { operationTypeList.value = it }
                }

                launch {
                    accountDao.getAllWithAllRelationsFlow().collect { accountList.value = it }
                }

                launch {
                    operationType.map {
                        val _operationTypeId = it?.let { OperationTypeId.fromId(it.id) }
                        MembersUIState.fromOperationType(_operationTypeId)
                    }.collect { membersUIState.value = it }
                }

                launch {
                    operationCategoryDao.getAllWithAllRelationsFlow()
                        .combine(operationType) { _operationCategoryList, _operationType ->
                            _operationCategoryList.filter { it.operationCategory.operationTypeId == _operationType?.id }
                        }.collect { operationCategoryList.value = it }
                }

                launch {
                    // TODO find the way to enable collect for selected accounts only for transfer operation type
                    combine(
                        currencyDao.getAllFlow(),
                        operationType,
                        fromAccount,
                        toAccount
                    ) { _currencyList, _type, _fromAccount, _toAccount ->
                        // transfer
                        if (_type?.id == OperationTypeId.TRANSFER.id) {
                            if (_fromAccount != null || _toAccount != null) {
                                _currencyList.filter {
                                    (_fromAccount != null) && it.id == (_fromAccount as AccountWithAllRelations).activeCurrency.id ||
                                            (_toAccount != null) && it.id == (_toAccount as AccountWithAllRelations).activeCurrency.id
                                }
                            } else {
                                emptyList()
                            }
                        }
                        // income or expense
                        else {
                            _currencyList
                        }
                    }.collect { currencyList.value = it }
                }

                launch {
                    combine(
                        sum,
                        currencyExchangeRate,
                    ) { _sum, _currencyExchangeRate ->
                        _sum * _currencyExchangeRate
                    }/*.takeWhile {
                        converterUIState.value == ConverterState.WITH_CONVERTER
                    }*/.collect {
                        converteredSum.value = it
                    }
                }

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

                        var _converterUIState: ConverterState = ConverterState.WITHOUT_CONVERTER
                        var _currencyExchangeRate: Double? = 1.0
                        val curName = currency?.name ?: ""
                        var _converterFromCurrency = curName
                        var _converterToCurrency = curName

                        // cases with converter
                        if (operationType != null && currencyList.isNotEmpty()) {
                            val operationTypeIdEnum = OperationTypeId.fromId(operationType.id)
                            if (operationTypeIdEnum != null) {
                                if (currencyList.size > 1) {
                                    when (operationTypeIdEnum) {
                                        OperationTypeId.INCOME -> {
                                            if (toAccount != null && currency != null) {
                                                val accountCurrencyId =
                                                    (toAccount as AccountWithAllRelations).activeCurrency.id
                                                if (currency.id != accountCurrencyId) {
                                                    //converterState = ConverterState.CHANGE_TARGET_CURRENCY
                                                    _converterUIState = ConverterState.WITH_CONVERTER
                                                    _currencyExchangeRate = currencyExchangeRateDao
                                                        .getCurrencyExchangeRateValueByCurrencyId(
                                                            currency.id,
                                                            accountCurrencyId
                                                        )
                                                    _converterToCurrency = toAccount.activeCurrency.name
                                                }
                                            }
                                        }

                                        OperationTypeId.EXPENSE -> {
                                            if (fromAccount != null && currency != null) {
                                                val accountCurrencyId =
                                                    (fromAccount as AccountWithAllRelations).activeCurrency.id
                                                if (currency.id != accountCurrencyId) {
                                                    _converterUIState = ConverterState.WITH_CONVERTER
                                                    _currencyExchangeRate = currencyExchangeRateDao
                                                        .getCurrencyExchangeRateValueByCurrencyId(
                                                            accountCurrencyId,
                                                            currency.id
                                                        )
                                                    _converterToCurrency =
                                                        fromAccount.activeCurrency.name
                                                }
                                            }
                                        }

                                        OperationTypeId.TRANSFER -> {
                                            if (fromAccount != null && toAccount != null && currency != null) {
                                                val fromAccountCurrencyId =
                                                    (fromAccount as AccountWithAllRelations).activeCurrency.id
                                                val toAccountCurrencyId =
                                                    (toAccount as AccountWithAllRelations).activeCurrency.id

                                                if (currency.id == fromAccountCurrencyId) {
                                                    //converterState = ConverterState.TRANSFER_FROM_CURRENCY
                                                    _converterUIState =
                                                        ConverterState.WITH_CONVERTER
                                                    _currencyExchangeRate =
                                                        db.currencyExchangeRateDao()
                                                            .getCurrencyExchangeRateValueByCurrencyId(
                                                                toAccountCurrencyId,
                                                                fromAccountCurrencyId
                                                            )
                                                    _converterToCurrency = toAccount.activeCurrency.name
                                                } else if (currency.id == toAccountCurrencyId) {
                                                    //converterState = ConverterState.TRANSFER_TO_CURRENCY
                                                    _converterUIState =
                                                        ConverterState.WITH_CONVERTER
                                                    _currencyExchangeRate =
                                                        db.currencyExchangeRateDao()
                                                            .getCurrencyExchangeRateValueByCurrencyId(
                                                                fromAccountCurrencyId,
                                                                toAccountCurrencyId
                                                            )
                                                    _converterToCurrency =
                                                        fromAccount.activeCurrency.name
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        converterUIState.value = _converterUIState
                        currencyExchangeRate.value = _currencyExchangeRate ?: 1.0
                        converterToCurrency.value = _converterToCurrency
                        converterFromCurrency.value = _converterFromCurrency

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
                currencyExchangeRate.value = operationData.operation.exchangeRateCoefficient
                sum.value = operationData.operation.sum
                note.value = ""
                return true
            } ?: return false
        }

        private fun createOperationEntityFromCurrentData(): OperationEntity? {
            val _id = currentOperationId?.let {
                if (it > 0) it else 0
            } ?: 0
            val _operationType = operationType.value
            val _fromAccount = fromAccount.value
            val _toAccount = toAccount.value
            val _operationCategory = operationCategory.value
            val _name = name.value
            val _sum = sum.value
            val _note = note.value
            val _currency = currency.value
            val _currencyExchangeRate = currencyExchangeRate.value
            if (_operationType != null && _sum != null && _note != null && _currency != null && _currency != null) {
                return OperationEntity(
                    id = _id,
                    operationTypeId = _operationType.id,
                    fromAccountId = _fromAccount?.id,
                    toAccountId = _toAccount?.id,
                    operationCategoryId = _operationCategory?.id,
                    sum = _sum,
                    name = _name,
                    exchangeRateCoefficient = _currencyExchangeRate,
                    activeCurrencyId = _currency.id,
                    dateTime = LocalDateTime.now(),
                    note = _note
                )
            } else
                return null
        }

        private suspend fun _leavePage() {
            viewModelScope.launch {
                leavePage.emit(Unit)
            }
        }

        private suspend fun create() {
            viewModelScope.launch {
                createOperationEntityFromCurrentData()?.let {
                    operationDao.insert(it)
                }
            }
        }

        private suspend fun delete() {
            currentOperationId?.let {
                viewModelScope.launch {
                    db.operationDao().deleteById(it)
                }
            }
        }

        // INPUTS
        override fun operationType(operationType: OperationTypeUIModel?) {
            this.operationType.value = operationType
        }

        override fun fromAccount(fromAccount: AccountUIModel?) {
            this.fromAccount.value = fromAccount
        }

        override fun toAccount(toAccount: AccountUIModel?) {
            this.toAccount.value = toAccount
        }

        override fun operationCategory(operationCategory: OperationCategoryUIModel?) {
            this.operationCategory.value = operationCategory
        }

        override fun name(name: String) {
            this.name.value = name
        }

        override fun currency(currency: CurrencyUIModel?) {
            this.currency.value = currency
        }

        override fun currencyExchangeRate(currencyExchangeRate: Double) {
            this.currencyExchangeRate.value = currencyExchangeRate
        }

        override fun sum(sum: Double) {
            this.sum.value = sum
        }

        override fun note(note: String) {
            this.note.value = note
        }

        override fun primaryActionClicked() {
            when (state.value) {
                State.NONE, State.INIT -> {}
                State.CREATE -> viewModelScope.launch {
                    create()
                    _leavePage()
                }
                State.INFO -> viewModelScope.launch {
                    delete()
                    _leavePage()
                }
            }
        }

        override fun secondaryActionClicked() {
            when (state.value) {
                State.NONE, State.INIT -> {}
                State.CREATE, State.INFO -> viewModelScope.launch {
                    _leavePage()
                }
            }
        }


        // OUTPUTS
        override fun uiState(): StateFlow<UIState> = uiState
        override fun operationTypeList(): StateFlow<List<OperationTypeUIModel>> = operationTypeList
        override fun operationType(): StateFlow<OperationTypeUIModel?> = operationType
        override fun accountList(): StateFlow<List<AccountUIModel>> = accountList
        override fun fromAccount(): StateFlow<AccountUIModel?> = fromAccount
        override fun toAccount(): StateFlow<AccountUIModel?> = toAccount
        override fun membersUIState(): StateFlow<MembersUIState> = membersUIState
        override fun operationCategoryList(): StateFlow<List<OperationCategoryUIModel>> =
            operationCategoryList

        override fun operationCategory(): StateFlow<OperationCategoryUIModel?> = operationCategory
        override fun name(): StateFlow<String> = name
        override fun currencyList(): StateFlow<List<CurrencyUIModel>> = currencyList
        override fun currency(): StateFlow<CurrencyUIModel?> = currency
        override fun note(): StateFlow<String> = note
        override fun converterUIState(): StateFlow<ConverterState> = converterUIState
        override fun converterFromCurrency(): StateFlow<String> = converterFromCurrency
        override fun converterToCurrency(): StateFlow<String> = converterToCurrency
        override fun currencyExchangeRate(): StateFlow<Double> = currencyExchangeRate
        override fun sum(): StateFlow<Double> = sum
        override fun converteredSum(): StateFlow<Double> = converteredSum
        override fun leavePage(): SharedFlow<Unit> = leavePage
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
                    "Назад",
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

            fun fromOperationType(operationTypeId: OperationTypeId?) = when (operationTypeId) {
                OperationTypeId.INCOME -> income()
                OperationTypeId.EXPENSE -> expense()
                OperationTypeId.TRANSFER -> transfer()
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