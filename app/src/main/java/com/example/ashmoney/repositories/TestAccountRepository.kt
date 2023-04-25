package com.example.ashmoney.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.Account
import com.example.ashmoney.models.Currency
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object TestAccountRepository {

    private val _list: MutableLiveData<MutableList<Account>> by lazy {
        val currencyList = TestCurrencyRepository.list.value
        val iconList = TestIconRepository.list.value
        val iconColorList = TestIconColorRepository.list.value

        MutableLiveData(
            if (currencyList != null && iconList != null && iconColorList != null) {
                mutableListOf(
                    Account("Наличные", 15.0, currencyList[0], iconList[4], iconColorList[0]),
                    Account("Банк", 20.0, currencyList[1], iconList[9], iconColorList[1]),
                    Account("Копилка", 30.0, currencyList[0], iconList[8], iconColorList[2])
                )
            } else {
                mutableListOf()
            }
        )
    }

    val list: LiveData<out List<Account>> = _list

    fun get(id: Int): Account? {
        _list.value?.let {
            return it.find { account ->
                account.id == id
            }
        } ?: return null
    }

    fun insert(account: Account) {
        _list.value?.let {
            val notExist = it.none { _account -> account.id == _account.id }
            if (notExist) {
                it.add(account)
                _list.postValue(it)
            }
        }
    }

    fun update(account: Account) {
        _list.value?.let {
            it.find { _account ->
                account.id == _account.id
            }?.let { _account ->
                _account.name = account.name
                _account.amountValue = account.amountValue
                _account.currency = account.currency
                _account.icon = account.icon
                _account.iconColor = account.iconColor
                _list.postValue(it)
            } ?: run {
                throw IllegalStateException()
            }
        }
    }

    fun delete(account: Account) {
        _list.value?.let {
            it.remove(account)
            _list.postValue(it)
        }
    }

    fun delete(accountId: Int) {
        _list.value?.let {
            val account = it.find { _account -> _account.id == accountId }
            account?.let { _account ->
                it.remove(_account)
                _list.postValue(it)
            }
        }
    }

}