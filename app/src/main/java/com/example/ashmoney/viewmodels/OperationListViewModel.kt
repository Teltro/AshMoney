package com.example.ashmoney.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.ui.OperationListUIModel2
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class OperationListViewModel : ViewModel() {

    val list: StateFlow<List<OperationListUIModel2>> =
        MainApp.instance.db.operationDao().getAllViewEntityFlow().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

}