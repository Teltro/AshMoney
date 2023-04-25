package com.example.ashmoney.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.databinding.RecyclerviewCurrencyExchangeRateItemBinding
import com.example.ashmoney.models.ui.CurrencyExchangeRateUIModel
import kotlin.math.roundToInt

class CurrencyExchangeRateHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var item: CurrencyExchangeRateUIModel
        private set

    private val binding = RecyclerviewCurrencyExchangeRateItemBinding.bind(itemView)

    var sum = 1.0
        set(value) {
            field = value
            refreshBuySellText()
        }

    fun bind(item: CurrencyExchangeRateUIModel, sum: Double) {
        this.item = item

        this.sum = sum
        binding.recyclerViewCurrencyExchangeRateCurrency.text = item.currencyName
    }

    private fun refreshBuySellText() {
        binding.recyclerViewCurrencyExchangeRate.text = ((item.rate * sum * 100).roundToInt() / 100.0).toString()
    }
}