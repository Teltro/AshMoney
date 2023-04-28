package com.example.ashmoney.viewmodels

import androidx.lifecycle.*
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
import java.util.Date

class OperationViewModel : ViewModel() {

    private val db
        get() = MainApp.instance.db

    val data: Data = Data()

    lateinit var state: MutableStateFlow<State>

    val selectedOperationType: MutableStateFlow<OperationTypeUIModel?> = MutableStateFlow(null)
    val selectedFromAccount: MutableStateFlow<AccountUIModel?> = MutableStateFlow(null)
    val selectedToAccount: MutableStateFlow<AccountUIModel?> = MutableStateFlow(null)
    val selectedCurrency: MutableStateFlow<CurrencyUIModel?> = MutableStateFlow(null)

    val operationTypeList: StateFlow<List<OperationTypeUIModel>> =
        db.operationTypeDao().getAllFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val accountList: StateFlow<List<AccountUIModel>> = db.accountDao().getAllWithAllRelationsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val operationCategoryList: StateFlow<List<OperationCategoryUIModel>> =
        db.operationCategoryDao()
            .getAllWithAllRelationsFlow()
            .combine(selectedOperationType) { operationCategoryList, selectedOperationType ->
                operationCategoryList.filter { it.operationCategory.operationTypeId == selectedOperationType?.id }
            }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /*@OptIn(ExperimentalCoroutinesApi::class)
    val operationCategoryList: StateFlow<List<OperationCategoryUIModel>> =
        selectedOperationType.flatMapLatest { operationType ->
            operationType?.let {
                db.operationCategoryDao().getWithAllRelationsFlowByOperationType(operationType.id)
            } ?: emptyFlow()
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())*/

    // TODO find the way to enable collect for selected accounts only for transfer operation type
    val currencyList: StateFlow<List<CurrencyUIModel>> =
        combine(
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

    val converterState: MutableStateFlow<ConverterState> = MutableStateFlow(ConverterState.WITH_CONVERTER)

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

    data class Data(
        var operationId: Int? = null,
        var operationType: OperationTypeUIModel? = null,
        var accountFrom: AccountUIModel? = null,
        var accountTo: AccountUIModel? = null,
        var operationCategory: OperationCategoryUIModel? = null,
        var name: String? = null,
        var amountValue: Double? = null,
        var currency: CurrencyUIModel? = null,
        var dateTime: String? = null,
        var currencyExchangeRate: Double? = null,
        var note: String? = null,
    ) {
        fun clean() {
            operationId = null
            operationType = null
            accountFrom = null
            accountTo = null
            operationCategory = null
            name = null
            amountValue = null
            currency = null
            currencyExchangeRate = null
            dateTime = null
            note = null
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
        /*WITH_DIRECT_CONVERTER,
        WITH_REVERSE_CONVERTER*/
        /*NONE,
        ONLY_ONE_CURRENCY,
        CHANGE_TARGET_CURRENCY,
        CHANGE_ANOTHER_CURRENCY,
        TRANSFER_FROM_CURRENCY,
        TRANSFER_TO_CURRENCY*/
    }


}