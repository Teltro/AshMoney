package com.example.ashmoney.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.data.account.AccountEntity
import com.example.ashmoney.data.account.AccountWithAllRelations
import com.example.ashmoney.data.activeCurrency.ActiveCurrencyEntity
import com.example.ashmoney.data.icon.IconEntity
import com.example.ashmoney.data.iconColor.IconColorEntity
import com.example.ashmoney.models.ui.CurrencyUIModel
import com.example.ashmoney.models.ui.IconColorUIModel
import com.example.ashmoney.models.ui.IconUIModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface AccountViewModel2 {

    interface Inputs {
        fun name(name: String)
        fun icon(icon: IconUIModel?)
        fun iconColor(iconColor: IconColorUIModel?)
        fun currency(currency: CurrencyUIModel?)
        fun sum(sum: Double)
        fun note(note: String)

        fun primaryActionClick()
        fun secondaryActionClick()
    }

    interface Outputs {
        fun uiState(): StateFlow<UIState>

        //fun state(): StateFlow<State>
        fun name(): StateFlow<String>
        fun iconList(): StateFlow<List<IconUIModel>>
        fun icon(): StateFlow<IconUIModel?>
        fun iconColorList(): StateFlow<List<IconColorUIModel>>
        fun iconColor(): StateFlow<IconColorUIModel?>
        fun currencyList(): StateFlow<List<CurrencyUIModel>>
        fun currency(): StateFlow<CurrencyUIModel?>
        fun sum(): StateFlow<Double>
        fun note(): StateFlow<String>
        fun leavePage(): SharedFlow<Unit>
    }

    class ViewModel : androidx.lifecycle.ViewModel(), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

        private val db
            get() = MainApp.instance.db

        private val accountDao = db.accountDao()
        private val iconDao = db.iconDao()
        private val iconColoDao = db.iconColorDao()
        private val currencyDao = db.activeCurrencyDao()

        private var currentAccountId: Int? = null
        private var accountEntity: AccountWithAllRelations? = null
        private var state = MutableStateFlow(State.NONE)
        private val uiState = MutableStateFlow<UIState>(UIState.none())
        private val name = MutableStateFlow("")
        private val icon = MutableStateFlow<IconUIModel?>(null)
        private val iconList = MutableStateFlow<List<IconUIModel>>(emptyList())
        private val iconColor = MutableStateFlow<IconColorUIModel?>(null)
        private val iconColorList = MutableStateFlow<List<IconColorUIModel>>(emptyList())
        private val currency = MutableStateFlow<CurrencyUIModel?>(null)
        private val currencyList = MutableStateFlow<List<CurrencyUIModel>>(emptyList())
        private val sum = MutableStateFlow(0.0)
        private val note = MutableStateFlow("")
        private val leavePage = MutableSharedFlow<Unit>()

        init {


            viewModelScope.launch {

                launch {
                    iconDao.getAllFlow().collect { iconList.value = it }
                }

                launch {
                    iconColoDao.getAllFlow().collect { iconColorList.value = it }
                }

                launch {
                    currencyDao.getAllFlow().collect { currencyList.value = it }
                }

                state.collect {
                    when (it) {
                        State.NONE, State.INIT, State.CREATE -> {
                            cleanAccountData()
                        }

                        State.INFO -> {
                            val result = loadAccountData();
                            if (!result)
                                state.value = State.NONE
                        }

                        else -> {}
                    }

                    uiState.value = UIState.fromState(it)
                }
            }
        }

        fun start(accountId: Int?) {
            if (currentAccountId != accountId) {
                currentAccountId = accountId
                state.run {
                    value = State.INIT
                    viewModelScope.launch {
                        when {
                            accountId == null -> value = State.NONE
                            accountId == -1 -> value = State.CREATE
                            accountId >= 0 -> value = State.INFO
                        }
                    }
                }
            }
        }

        private fun cleanAccountData() {
            name.value = ""
            sum.value = 0.0
            note.value = ""
            currency.value = null
            icon.value = null
            iconColor.value = null
        }

        private suspend fun loadAccountData(): Boolean {
            currentAccountId?.let { id ->
                accountEntity = accountDao.getWithAllRelationsById(id)
                accountEntity
            }?.let { accountData ->
                name.value = accountData.account.name
                sum.value = accountData.account.amountValue
                note.value = ""
                currency.value = accountData.activeCurrency
                icon.value = accountData.icon
                iconColor.value = accountData.iconColor
                //state = State.INFO
                return true
            } ?: return false
        }

        private suspend fun _leavePage() {
            viewModelScope.launch {
                leavePage.emit(Unit)
            }
        }

        private suspend fun create() {
            viewModelScope.launch {
                createAccountEntityFromCurrentData()?.let {
                    accountDao.insert(it)
                }
            }
        }

        private suspend fun update() {
            viewModelScope.launch {
                createAccountEntityFromCurrentData()?.let {
                    accountDao.update(it)
                }
            }
        }

        private suspend fun delete() {
            currentAccountId?.let {
                viewModelScope.launch {
                    db.accountDao().deleteById(it)
                }
            }
        }

        private fun createAccountEntityFromCurrentData(): AccountEntity? {
            val _id = currentAccountId?.let {
                if (it > 0) it else 0
            } ?: 0
            val _accountName = name.value
            val _amountValue = sum.value
            val _note = note.value
            val _currency = currency.value
            val _icon = icon.value
            val _iconColor = iconColor.value
            if (_accountName != null && _amountValue != null && _note != null && _currency != null && _icon != null && _iconColor != null) {
                return AccountEntity(
                    id = _id,
                    name = _accountName,
                    amountValue = _amountValue,
                    activeCurrencyId = (_currency as ActiveCurrencyEntity).id,
                    iconId = (_icon as IconEntity).id,
                    iconColorId = (_iconColor as IconColorEntity).id,
                    //note = _note
                )
            } else
                return null
        }


        override fun name(name: String) {
            this.name.value = name
        }

        override fun icon(icon: IconUIModel?) {
            this.icon.value = icon
        }

        override fun iconColor(iconColor: IconColorUIModel?) {
            this.iconColor.value = iconColor
        }

        override fun currency(currency: CurrencyUIModel?) {
            this.currency.value = currency
        }

        override fun sum(sum: Double) {
            this.sum.value = sum
        }

        override fun note(note: String) {
            this.note.value = note
        }

        override fun primaryActionClick() {
            when (state.value) {
                State.NONE, State.INIT -> {}
                State.INFO -> state.value = State.UPDATE
                State.CREATE -> viewModelScope.launch {
                    create()
                    _leavePage()
                }

                State.UPDATE -> {
                    viewModelScope.launch {
                        update()
                    }
                    state.value = State.INFO
                }
            }
        }

        override fun secondaryActionClick() {
            when (state.value) {
                State.NONE, State.INIT -> {}
                State.INFO -> viewModelScope.launch {
                    delete()
                    _leavePage()
                }

                State.CREATE -> viewModelScope.launch { _leavePage() }
                State.UPDATE -> state.value = State.INFO
            }
        }


        override fun uiState(): StateFlow<UIState> = uiState

        //override fun state(): StateFlow<State> = state
        override fun name(): StateFlow<String> = name
        override fun iconList(): StateFlow<List<IconUIModel>> = iconList
        override fun icon(): StateFlow<IconUIModel?> = icon
        override fun iconColorList(): StateFlow<List<IconColorUIModel>> = iconColorList
        override fun iconColor(): StateFlow<IconColorUIModel?> = iconColor
        override fun currencyList(): StateFlow<List<CurrencyUIModel>> = currencyList
        override fun currency(): StateFlow<CurrencyUIModel?> = currency
        override fun sum(): StateFlow<Double> = sum
        override fun note(): StateFlow<String> = note
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
                    "Изменить",
                    "Удалить"
                )

            fun create(): UIState =
                UIState(
                    true,
                    "Создать",
                    "Отменить"
                )

            fun update(): UIState =
                UIState(
                    true,
                    "Применить",
                    "Отменить"
                )

            fun none(): UIState =
                UIState(
                    false,
                    "",
                    ""
                )

            fun fromState(state: State): UIState {
                return when (state) {
                    State.NONE, State.INIT -> none()
                    State.INFO -> info()
                    State.CREATE -> create()
                    State.UPDATE -> update()
                }
            }
        }

    }

    enum class State {
        NONE,
        INIT,
        INFO,
        CREATE,
        UPDATE,
    }
}

