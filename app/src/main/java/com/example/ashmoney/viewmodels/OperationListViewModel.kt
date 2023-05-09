package com.example.ashmoney.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.ui.OperationListUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface OperationListViewModel {

    interface Inputs {

    }

    interface Outputs {
        fun operationList(): StateFlow<List<OperationListUIModel>>
    }

    class ViewModel : androidx.lifecycle.ViewModel(), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

        val operationDao = MainApp.instance.db.operationDao()

        private val operationList = MutableStateFlow<List<OperationListUIModel>>(emptyList())

        init {
            viewModelScope.launch {
                operationDao.getAllViewEntityFlow().collect {
                    operationList.value = it
                }
            }
        }

        override fun operationList(): StateFlow<List<OperationListUIModel>> = operationList

    }
}