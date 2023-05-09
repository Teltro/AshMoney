package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.ashmoney.R
import com.example.ashmoney.holder.CurrencyExchangeRateHolder
import com.example.ashmoney.models.ui.CurrencyExchangeRateUIModel

class CurrencyExchangeRateAdapter :
    ListAdapter<CurrencyExchangeRateUIModel, CurrencyExchangeRateHolder>(
        RecyclerViewUIModelDiffCallback()
    ) {

    var convertingSum: Double = 1.0
        set(value) {
            sumUpdateSubscribedSet.forEach { it.sum = value }
            field = value
        }

    private val sumUpdateSubscribedSet: MutableSet<CurrencyExchangeRateHolder> = mutableSetOf()

    override fun onCurrentListChanged(
        previousList: MutableList<CurrencyExchangeRateUIModel>,
        currentList: MutableList<CurrencyExchangeRateUIModel>
    ) {
        super.onCurrentListChanged(previousList, currentList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyExchangeRateHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_currency_exchange_rate_item, parent, false)

        return CurrencyExchangeRateHolder(itemView)
    }

    override fun onBindViewHolder(holder: CurrencyExchangeRateHolder, position: Int) {
        val item = currentList[position]

        holder.bind(item, convertingSum)
        sumUpdateSubscribedSet.add(holder)
    }

    override fun onViewRecycled(holder: CurrencyExchangeRateHolder) {
        super.onViewRecycled(holder)
        sumUpdateSubscribedSet.remove(holder)
    }
}