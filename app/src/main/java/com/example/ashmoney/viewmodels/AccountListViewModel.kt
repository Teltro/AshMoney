package com.example.ashmoney.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.ui.AccountUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface AccountListViewModel {

    interface Inputs {

    }

    interface Outputs {
        fun accountList(): StateFlow<List<AccountUIModel>>
    }

    class ViewModel : androidx.lifecycle.ViewModel(), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

        val accountDao = MainApp.instance.db.accountDao()

        private val accountList = MutableStateFlow<List<AccountUIModel>>(emptyList())

        init {
            viewModelScope.launch {
                accountDao.getAllWithAllRelationsFlow().collect {
                    accountList.value = it
                }
            }
        }

        override fun accountList(): StateFlow<List<AccountUIModel>> = accountList

    }

}