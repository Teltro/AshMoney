package com.example.ashmoney.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ashmoney.models.OperationCategory
import com.example.ashmoney.models.OperationType

object TestOperationCategoryRepository {

    private val _list: MutableLiveData<MutableList<OperationCategory>> by lazy {
        val operationTypeList = TestOperationTypeRepository.list.value
        val iconList = TestIconRepository.list.value
        val iconColorList = TestIconColorRepository.list.value

        if (
            operationTypeList != null && !operationTypeList.isEmpty() &&
            iconList != null && !iconList.isEmpty() &&
            iconColorList != null && !iconColorList.isEmpty()
        ) {

            MutableLiveData(
                mutableListOf(
                    OperationCategory("Продукты", operationTypeList[1], iconList[5], iconColorList[0]),
                    OperationCategory("Налоги", operationTypeList[1], iconList[9], iconColorList[1]),
                    OperationCategory("Транспорт", operationTypeList[1], iconList[7], iconColorList[2]),

                    OperationCategory("Работа", operationTypeList[0], iconList[2], iconColorList[3]),
                    OperationCategory("Акции", operationTypeList[0], iconList[1], iconColorList[4])
                )
            )
        } else
            MutableLiveData(
                mutableListOf()
            )
    }

    val list: LiveData<out List<OperationCategory>>
        get() = _list

    fun get(id: Int): OperationCategory? {
        return _list.value?.let {
             it.find { operationCategory -> operationCategory.id == id }
        }
    }

    fun getListByOperationType(operationType: OperationType): LiveData<out List<OperationCategory>>? {
        val filteredList = _list.value?.filter { item -> item.type == operationType }
        return filteredList?.let {
            MutableLiveData(filteredList)
        }
    }

}