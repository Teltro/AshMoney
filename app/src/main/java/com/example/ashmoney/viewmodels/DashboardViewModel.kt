package com.example.ashmoney.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.ui.CurrencyUIModel

class DashboardViewModel : ViewModel() {

    private val db
        get() = MainApp.instance.db

    val data: Data = Data()

    data class Data(
        var totalSum: Double? = null,
        var globalCurrency: CurrencyUIModel? = null,
    )

}