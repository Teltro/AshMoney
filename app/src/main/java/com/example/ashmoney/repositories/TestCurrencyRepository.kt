package com.example.ashmoney.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ashmoney.models.Currency

object TestCurrencyRepository {

    /*companion object {
        val instance by lazy {
            TestCurrencyRepository()
        }
    }*/

    val list: LiveData<List<Currency>> by lazy {
        MutableLiveData(listOf(
            Currency("BYN", "933"),
            Currency("USD", "840")
        ))
    }

    fun get(id: Int): Currency? {
        return list.value?.let {
            it.find { currency -> currency.id == id }
        }
    }

}