package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.R
import com.example.ashmoney.models.Currency
import kotlin.math.max

class CurrencyAdapter(
    currentCurrency: Currency?,
    private val onCurrencySelected: (Currency) -> Unit
) : ListAdapter<Currency, CurrencyAdapter.ViewHolder>(DiffCallBack()) {

    var selectedPosition = 0;
    var selectedCurrency: Currency?
        get() = currentList[selectedPosition]
        set(value) {
            val newPosition = max(0, currentList.indexOf(value))

            if(newPosition != selectedPosition) {
                refreshSelection(newPosition)
            }
        }

    var isEnabled = true
    set(value) {
        field = value
        notifyDataSetChanged() //??
    }


    init {
        selectedCurrency = currentCurrency
    }

    class DiffCallBack: DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.name == newItem.name
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyRadioButton = itemView as RadioButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_currency_item, parent, false)
        return CurrencyAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.currencyRadioButton.let {
            it.isEnabled = isEnabled

            it.text = currentList[position].name

            it.isChecked = position == selectedPosition
            if(it.isChecked)
                onCurrencySelected.invoke(currentList[position])

            it.setOnClickListener {
                refreshSelection(position)
            }

        }
    }

    private fun refreshSelection(newPosition: Int) {
        val oldSelectedPosition = selectedPosition
        selectedPosition = newPosition
        notifyItemChanged(oldSelectedPosition)
        notifyItemChanged(selectedPosition)
    }

}