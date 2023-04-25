package com.example.ashmoney.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ashmoney.models.OperationType

object TestOperationTypeRepository {

    private val _list: MutableLiveData<MutableList<OperationType>> by lazy {
        MutableLiveData(mutableListOf(
            OperationType("Доход"),
            OperationType("Расход"),
            OperationType("Перевод")
        ))
    }

    val list: LiveData<out List<OperationType>>
        get() = _list

    fun get(id: Int): OperationType? {
        _list.value?.let {
            return it.find { operationType -> operationType.id == id }
        } ?: return null
    }

}