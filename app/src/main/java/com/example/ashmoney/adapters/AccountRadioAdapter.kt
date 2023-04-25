package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.ashmoney.R
import com.example.ashmoney.holder.AccountRadioHolder
import com.example.ashmoney.models.ui.AccountUIModel

class AccountRadioAdapter(
    selectFirstItemIfNotExist: Boolean = false,
    selectionChangeListener: ((selectedItem: AccountUIModel?) -> Unit)? = null
) : RadioAdapter<AccountUIModel, AccountRadioHolder>(selectFirstItemIfNotExist, selectionChangeListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountRadioHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_radio_icon, parent, false)

        if (itemView is ImageView)
            return AccountRadioHolder(itemView)
        else
            throw IllegalStateException()
    }


}