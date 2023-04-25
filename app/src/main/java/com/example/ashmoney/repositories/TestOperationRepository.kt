package com.example.ashmoney.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ashmoney.models.Account
import com.example.ashmoney.models.Currency
import com.example.ashmoney.models.Operation

object TestOperationRepository {

    private val _list: MutableLiveData<MutableList<Operation>> by lazy {
        val belCurrency = TestCurrencyRepository.get(1);
        val usdCurrency = TestCurrencyRepository.get(2);

        val incomeType = TestOperationTypeRepository.get(1);
        val expenseType = TestOperationTypeRepository.get(2);
        val transferType = TestOperationTypeRepository.get(3);

        val productsCategory = TestOperationCategoryRepository.get(1)
        val taxCategory = TestOperationCategoryRepository.get(2)
        val transportCategory = TestOperationCategoryRepository.get(3)
        val workCategory = TestOperationCategoryRepository.get(4)
        val shareCategory = TestOperationCategoryRepository.get(5)

        if (
            belCurrency != null &&
            usdCurrency != null &&

            incomeType != null &&
            expenseType != null &&
            transferType != null &&

            productsCategory != null &&
            taxCategory != null &&
            transportCategory != null &&
            workCategory != null &&
            shareCategory != null
        ) {
            MutableLiveData(
                mutableListOf(
                    Operation(
                        null,
                        TestAccountRepository.get(1),
                        null,
                        5.0,
                        belCurrency,
                        incomeType,
                        workCategory,
                        null
                    ),
                    Operation(
                        null,
                        TestAccountRepository.get(1),
                        null,
                        15.0,
                        belCurrency,
                        incomeType,
                        shareCategory,
                        null
                    ),
                    Operation(
                        null,
                        null,
                        TestAccountRepository.get(1),
                        2.0,
                        belCurrency,
                        expenseType,
                        transportCategory,
                        null
                    ),
                    Operation(
                        null,
                        null,
                        TestAccountRepository.get(2),
                        3.0,
                        belCurrency,
                        expenseType,
                        productsCategory,
                        null
                    ),
                    Operation(
                        null,
                        TestAccountRepository.get(3),
                        TestAccountRepository.get(2),
                        4.0,
                        belCurrency,
                        transferType,
                        null,
                        null
                    ),
                )
            )
        } else {
            MutableLiveData()
        }
    }

    val list: LiveData<out List<Operation>>
        get() = _list

    fun get(id: Int): Operation? {
        _list.value?.let {
            return it.find { operation ->
                operation.id == id
            }
        } ?: return null
    }

    fun insert(operation: Operation) {
        _list.value?.let {
            val notExist = it.none { _operation -> operation.id == _operation.id }
            if (notExist) {
                it.add(operation)
                _list.postValue(it)
            }
        }
    }

    fun delete(operation: Operation) {
        _list.value?.let {
            it.remove(operation)
            _list.postValue(it)
        }
    }

    fun delete(operationId: Int) {
        _list.value?.let {
            val operation = it.find { _operation -> _operation.id == operationId }
            operation?.let { _operation ->
                it.remove(_operation)
                _list.postValue(it)
            }
        }
    }


}