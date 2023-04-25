package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.ashmoney.R
import com.example.ashmoney.holder.CurrencyRadioHolder
import com.example.ashmoney.models.ui.AccountUIModel
import com.example.ashmoney.models.ui.CurrencyUIModel

class CurrencyRadioAdapter(
    selectFirstItemIfNotExist: Boolean = false,
    selectionChangeListener: ((selectedItem: CurrencyUIModel?) -> Unit)? = null
) : RadioAdapter<CurrencyUIModel, CurrencyRadioHolder>(selectFirstItemIfNotExist, selectionChangeListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRadioHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_radio_text, parent, false)

        if (itemView is TextView)
            return CurrencyRadioHolder(itemView)
        else
            throw IllegalStateException()
    }
}