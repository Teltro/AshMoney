package com.example.ashmoney.viewmodels

import androidx.lifecycle.ViewModel
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

class AccountViewModel : ViewModel() {

    private val db
        get() = MainApp.instance.db

    val data: Data = Data()

    val state = MutableStateFlow(State.NONE)

    val currencyList: StateFlow<List<CurrencyUIModel>> =
        db.activeCurrencyDao().getAllFlow()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val iconList: StateFlow<List<IconUIModel>> =
        db.iconDao().getAllFlow().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val iconColorList: StateFlow<List<IconColorUIModel>> =
        db.iconColorDao().getAllFlow().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun start(accountId: Int?) {
        if (data.accountId == accountId)
            return
        else {
            state.value = State.INIT
            viewModelScope.launch {
                when {
                    accountId == null -> startNoneState()
                    accountId == -1 -> startCreateState()
                    accountId >= 0 -> startInfoState(accountId)
                }
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

    private suspend fun startInfoState(accountId: Int) {
        db.accountDao().getWithAllRelationsById(accountId)?.run {
            data.let {
                it.accountId = accountId
                it.accountName = account.name
                it.amountValue = account.amountValue
                it.note = ""
                it.currency = activeCurrency
                it.icon = icon
                it.iconColor = iconColor
            }
            state.value = State.INFO
        } ?: run {
            startNoneState()
        }
    }

    // maybe insert and update here too?
    fun getAccountFromCurrentData(): AccountEntity? {
        with(data) {
            val _id = accountId?.let {
                if (it > 0)
                    it
                else
                    0
            } ?: 0
            val _accountName = accountName
            val _amountValue = amountValue
            //val _note = note
            val _currency = currency
            val _icon = icon
            val _iconColor = iconColor

            if (_accountName != null && _amountValue != null && /*_note != null &&*/ _currency != null && _icon != null && _iconColor != null) {
                return AccountEntity(
                    id = _id,
                    name = _accountName,
                    amountValue = _amountValue,
                    activeCurrencyId = (_currency as ActiveCurrencyEntity).id,
                    iconId = (_icon as IconEntity).id,
                    iconColorId = (_iconColor as IconColorEntity).id
                )
            } else {
                return null
            }
        }
    }

    fun insertAccount(account: AccountEntity) {
        viewModelScope.launch {
            db.accountDao().insert(account)
        }
    }

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            db.accountDao().update(account)
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            db.accountDao().delete(account)
        }
    }

    fun deleteAccount2(accountId: Int) {
        viewModelScope.launch {
            db.accountDao().deleteById(accountId)
        }
    }

    data class Data(
        var accountId: Int? = null,
        var accountName: String? = null,
        var amountValue: Double? = null,
        var note: String? = null,
        var currency: CurrencyUIModel? = null,
        var icon: IconUIModel? = null,
        var iconColor: IconColorUIModel? = null,
    ) {
        fun clean() {
            accountId = null
            accountName = null
            amountValue = null
            note = null
            currency = null
            icon = null
            iconColor = null
        }
    }

    enum class State {
        NONE,
        INIT,
        INFO,
        CREATE,
        UPDATE,
        ERROR,
    }

}