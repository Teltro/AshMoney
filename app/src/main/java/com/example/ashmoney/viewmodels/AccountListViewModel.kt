package com.example.ashmoney.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.ui.AccountUIModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AccountListViewModel: ViewModel() {

    val list: StateFlow<List<AccountUIModel>> =
        MainApp.instance.db.accountDao().getAllWithAllRelationsFlow().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

}