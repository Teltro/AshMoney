package com.example.ashmoney.viewmodels

import androidx.compose.runtime.collectAsState
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

        private var state: MutableStateFlow<State> = MutableStateFlow(State.NONE)

        private val uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.none())

        private val operationTypeList: StateFlow<List<OperationTypeUIModel>> =
            operationTypeDao.getAllFlow()
                .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        private val operationType: MutableStateFlow<OperationTypeUIModel?> = MutableStateFlow(null)

        private val membersUIState: StateFlow<MembersUIState> = operationType.map { it ->
            val operationType = it?.let { OperationType.fromId(it.id) }
            MembersUIState.fromOperationType(operationType)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, MembersUIState.none())

        private val accountList: StateFlow<List<AccountUIModel>> =
            accountDao.getAllWithAllRelationsFlow()
                .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        private val fromAccount: MutableStateFlow<AccountUIModel?> = MutableStateFlow(null)
        private val toAccount: MutableStateFlow<AccountUIModel?> = MutableStateFlow(null)
        private val operationCategoryList: StateFlow<List<OperationCategoryUIModel>> =
            operationCategoryDao.getAllWithAllRelationsFlow()
                .combine(operationType) { operationCategoryList, selectedOperationType ->
                    operationCategoryList.filter { it.operationCategory.operationTypeId == selectedOperationType?.id }
                }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        private val operationCategory: MutableStateFlow<OperationCategoryUIModel?> =
            MutableStateFlow(null)

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

        private val currency: MutableStateFlow<CurrencyUIModel?> = MutableStateFlow(null)
        private val converterFromCurrency: MutableStateFlow<CurrencyUIModel?> =
            MutableStateFlow(null)
        private val converterToCurrency: MutableStateFlow<CurrencyUIModel?> = MutableStateFlow(null)
        private val exchangeRate: MutableStateFlow<Double> = MutableStateFlow(1.0)
        private val sum: MutableStateFlow<Double> = MutableStateFlow(0.0)
        private val name: MutableStateFlow<String> = MutableStateFlow("")
        private val note: MutableStateFlow<String> = MutableStateFlow("")
        private val converterState: MutableStateFlow<ConverterState> =
            MutableStateFlow(ConverterState.NONE)


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
        override fun state(): StateFlow<State> = state

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

}

data class MembersUIState(
    val fromAccountVisible: Boolean,
    val toAccountVisible: Boolean,
    val operationCategoryVisible: Boolean
) {
    companion object {
        fun income() = MembersUIState(false, true, true)
        fun expense() = MembersUIState(true, false, true)
        fun transfer() = MembersUIState(true, true, false)
        fun none() = MembersUIState(false, false, false)
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