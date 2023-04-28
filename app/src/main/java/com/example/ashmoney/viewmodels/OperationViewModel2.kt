package com.example.ashmoney.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.data.account.AccountWithAllRelations
import com.example.ashmoney.data.operation.OperationEntity
import com.example.ashmoney.data.operationCategory.OperationCategoryWithAllRelations
import com.example.ashmoney.data.operationType.OperationTypeEntity
import com.example.ashmoney.models.ui.AccountUIModel
import com.example.ashmoney.models.ui.CurrencyUIModel
import com.example.ashmoney.models.ui.OperationCategoryUIModel
import com.example.ashmoney.models.ui.OperationTypeUIModel
import com.example.ashmoney.utils.OperationType
import com.example.ashmoney.utils.toIsoString
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

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


        fun cancelClicked()
        fun deleteClicked()
        fun createClicked()
    }

    interface Outputs {
        fun state(): StateFlow<State>
        fun operationTypeList(): StateFlow<List<OperationTypeUIModel>>
        fun selectedOperationType(): OperationTypeUIModel?
        fun accountList(): StateFlow<List<AccountUIModel>>
        fun selectedFromAccount(): AccountUIModel
        fun selectedToAccount(): AccountUIModel
        fun operationCategoryList(): StateFlow<List<OperationCategoryUIModel>>
        fun selectedOperationCategory(): OperationCategoryUIModel
        fun name(): String
        fun currencyList(): StateFlow<List<CurrencyUIModel>>
        fun selectedCurrency(): CurrencyUIModel
        fun note(): String
        fun converterState(): StateFlow<ConverterState>
        fun converterFromCurrency(): StateFlow<CurrencyUIModel>
        fun converterToCurrency(): StateFlow<CurrencyUIModel>
        fun exchangeRate(): StateFlow<Double>
        fun sum(): Double
    }


    class ViewModel: androidx.lifecycle.ViewModel(), Inputs, Outputs {

        private val db
            get() = MainApp.instance.db

        lateinit var state: MutableStateFlow<State>

        val selectedOperationType: MutableStateFlow<OperationTypeUIModel?>
        val operationTypeList: StateFlow<List<OperationTypeUIModel>>
        val selectedFromAccount: MutableStateFlow<AccountUIModel?>
        val selectedToAccount: MutableStateFlow<AccountUIModel?>
        val accountList: StateFlow<List<AccountUIModel>>
        val selectedOperationCategory: MutableStateFlow<OperationCategoryUIModel?>
        val operationCategoryList: StateFlow<List<OperationCategoryUIModel>>
        val selectedCurrency: MutableStateFlow<CurrencyUIModel?>
        // TODO find the way to enable collect for selected accounts only for transfer operation type
        val currencyList: StateFlow<List<CurrencyUIModel>>
        val converterState: MutableStateFlow<ConverterState>

        private val dataFlow: Flow<Unit> = combine(
            selectedOperationType,
            selectedFromAccount,
            selectedToAccount,
            currencyList,
            selectedCurrency
        ) { selectedOperationType, fromAccount, toAccount, currencyList, selectedCurrency ->
            //var converterState = ConverterState.NONE
            var converterState = ConverterState.WITHOUT_CONVERTER
            var currencyExchangeRate: Double? = 1.0

            if (selectedOperationType != null && currencyList.isNotEmpty()) {
                val operationType = OperationType.fromId(selectedOperationType.id)
                if (operationType != null) {
                    if (currencyList.size == 1) {
                        //converterState = ConverterState.ONLY_ONE_CURRENCY
                        converterState = ConverterState.WITHOUT_CONVERTER
                    } else {
                        when (operationType) {
                            OperationType.INCOME -> {
                                if (toAccount != null && selectedCurrency != null) {
                                    val accountCurrencyId =
                                        (toAccount as AccountWithAllRelations).activeCurrency.id
                                    if (selectedCurrency.id == accountCurrencyId) {
                                        //converterState = ConverterState.CHANGE_TARGET_CURRENCY
                                        converterState = ConverterState.WITHOUT_CONVERTER
                                    } else {
                                        //converterState = ConverterState.CHANGE_ANOTHER_CURRENCY
                                        converterState = ConverterState.WITH_CONVERTER
                                        currencyExchangeRate = db.currencyExchangeRateDao()
                                            .getCurrencyExchangeRateValueByCurrencyId(
                                                selectedCurrency.id,
                                                accountCurrencyId
                                            )
                                    }
                                }
                            }

                            OperationType.EXPENSE -> {
                                if (fromAccount != null && selectedCurrency != null) {
                                    val accountCurrencyId =
                                        (fromAccount as AccountWithAllRelations).activeCurrency.id
                                    if (selectedCurrency.id == accountCurrencyId) {
                                        //converterState = ConverterState.CHANGE_TARGET_CURRENCY
                                        converterState = ConverterState.WITHOUT_CONVERTER
                                    } else {
                                        //converterState = ConverterState.CHANGE_ANOTHER_CURRENCY
                                        converterState = ConverterState.WITH_CONVERTER
                                        currencyExchangeRate = db.currencyExchangeRateDao()
                                            .getCurrencyExchangeRateValueByCurrencyId(
                                                accountCurrencyId,
                                                selectedCurrency.id
                                            )
                                    }
                                }
                            }

                            OperationType.TRANSFER -> {
                                if (fromAccount != null && toAccount != null && selectedCurrency != null) {
                                    val accountFromCurrencyId =
                                        (fromAccount as AccountWithAllRelations).activeCurrency.id
                                    val accountToCurrencyId =
                                        (toAccount as AccountWithAllRelations).activeCurrency.id

                                    if (selectedCurrency.id == accountFromCurrencyId) {
                                        //converterState = ConverterState.TRANSFER_FROM_CURRENCY
                                        converterState = ConverterState.WITH_CONVERTER
                                        currencyExchangeRate = db.currencyExchangeRateDao()
                                            .getCurrencyExchangeRateValueByCurrencyId(
                                                accountToCurrencyId,
                                                accountFromCurrencyId
                                            )
                                    } else if (selectedCurrency.id == accountToCurrencyId) {
                                        //converterState = ConverterState.TRANSFER_TO_CURRENCY
                                        converterState = ConverterState.WITH_CONVERTER
                                        currencyExchangeRate = db.currencyExchangeRateDao()
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

            data.currencyExchangeRate = currencyExchangeRate
            this.converterState.value = converterState
        }

        init {
            //state =

            selectedOperationType = MutableStateFlow(null)

            operationTypeList = db.operationTypeDao().getAllFlow()
                .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

            selectedFromAccount = MutableStateFlow(null)

            selectedToAccount = MutableStateFlow(null)

            accountList = db.accountDao().getAllWithAllRelationsFlow()
                .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

            selectedOperationCategory = MutableStateFlow(null)

            operationCategoryList = db.operationCategoryDao()
                .getAllWithAllRelationsFlow()
                .combine(selectedOperationType) { operationCategoryList, selectedOperationType ->
                    operationCategoryList.filter { it.operationCategory.operationTypeId == selectedOperationType?.id }
                }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

            selectedCurrency = MutableStateFlow(null)

            // TODO find the way to enable collect for selected accounts only for transfer operation type
            currencyList = combine(
                db.activeCurrencyDao().getAllFlow(),
                selectedOperationType,
                selectedFromAccount,
                selectedToAccount
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

            converterState = MutableStateFlow(ConverterState.WITHOUT_CONVERTER)
        }

        fun start(operationId: Int?) {
            if (data.operationId == operationId)
                return
            else {
                state = MutableStateFlow(State.INIT)
                viewModelScope.launch {

                    when {
                        operationId == null -> startNoneState()
                        operationId == -1 -> startCreateState()
                        operationId >= 0 -> startInfoState(operationId)
                    }

                    dataFlow.collect()

                }
            }
        }

        private fun startNoneState() {
            data.clean()
            state.value = State.NONE
        }

        private fun startCreateState() {
            data.clean()
            state.value = State.CREATE
        }

        private suspend fun startInfoState(operationId: Int) {
            db.operationDao().getWithAllRelationsById(operationId)?.run {
                data.let {
                    it.operationId = operationId
                    it.operationType = operationType
                    it.accountFrom = fromAccount
                    it.accountTo = toAccount
                    it.operationCategory = operationCategory
                    it.name = operation.name
                    it.currency = activeCurrency
                    it.dateTime = operation.dateTime
                    it.currencyExchangeRate = operation.exchangeRateCoefficient
                    it.amountValue = operation.sum
                    it.note = operation.note
                }
                selectedOperationType.value = operationType
                state.value = State.INFO
            } ?: run {
                // TODO error? back?
                startNoneState()
            }
        }

        fun getOperationFromCurrentData(): OperationEntity? {
            with(data) {
                val _id = operationId?.let {
                    if (it > 0) it
                    else 0
                } ?: 0
                val _operationType = operationType
                val _accountFrom = accountFrom
                val _accountTo = accountTo
                val _operationCategory = operationCategory
                val _name = name
                val _amountValue = amountValue
                val _currency = currency
                val _note = note
                if (
                    _operationType != null &&
                    _name != null &&
                    _amountValue != null &&
                    _currency != null &&
                    _note != null
                ) {
                    return OperationEntity(
                        id = _id,
                        name = _name,
                        operationTypeId = (_operationType as OperationTypeEntity).id,
                        toAccountId = (_accountTo as? AccountWithAllRelations)?.id,
                        fromAccountId = (_accountFrom as? AccountWithAllRelations)?.id,
                        operationCategoryId = (_operationCategory as? OperationCategoryWithAllRelations)?.id,
                        activeCurrencyId = 1,
                        sum = _amountValue,
                        dateTime = Date().toIsoString(),
                        note = _note,
                    )
                } else {
                    return null
                }
            }
        }

        private suspend fun getOperationById2(operationId: Int): OperationEntity? {
            return db.operationDao().getById(operationId)
        }

        fun insertOperation(operation: OperationEntity) {
            viewModelScope.launch {
                db.operationDao().insert(operation)
            }
        }

        fun deleteOperation(operation: OperationEntity) {
            viewModelScope.launch {
                db.operationDao().delete(operation)
            }
        }

        fun deleteOperation(operationId: Int) {
            viewModelScope.launch {
                db.operationDao().deleteById(operationId)
            }
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

        override fun cancelClicked() {
            TODO("Not yet implemented")
        }

        override fun deleteClicked() {
            TODO("Not yet implemented")
        }

        override fun createClicked() {
            TODO("Not yet implemented")
        }


        // OUTPUTS
        override fun state(): StateFlow<State> = state

        override fun operationTypeList(): StateFlow<List<OperationTypeUIModel>> = operationTypeList
        override fun selectedOperationType(): OperationTypeUIModel? = selectedOperationType

        override fun accountList(): StateFlow<List<AccountUIModel>> = accountList
        override fun selectedFromAccount(): AccountUIModel = selectedFromAccount
        override fun selectedToAccount(): AccountUIModel = selectedToAccount

        override fun operationCategoryList(): StateFlow<List<OperationCategoryUIModel>> = operationCategoryList
        override fun selectedOperationCategory(): OperationCategoryUIModel = selectedOperationCategory

        override fun name(): String {
        }

        override fun currencyList(): StateFlow<List<CurrencyUIModel>> = currencyList
        override fun selectedCurrency(): CurrencyUIModel = selectedCurrency

        override fun note(): String {
        }

        override fun converterState(): StateFlow<ConverterState> = converterState

        override fun converterFromCurrency(): StateFlow<CurrencyUIModel> {
        }

        override fun converterToCurrency(): StateFlow<CurrencyUIModel> {
        }

        override fun exchangeRate(): StateFlow<Double> {
        }

        override fun sum(): Double {
        }
    }

        enum class State {
            NONE,
            INIT,
            CREATE,
            INFO
        }

        enum class ConverterState {
            WITHOUT_CONVERTER,
            WITH_CONVERTER,
        }
}