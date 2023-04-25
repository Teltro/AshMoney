package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.ashmoney.R
import com.example.ashmoney.holder.IconColorRadioHolder
import com.example.ashmoney.holder.IconRadioHolder
import com.example.ashmoney.models.ui.CurrencyUIModel
import com.example.ashmoney.models.ui.IconColorUIModel

class IconColorRadioAdapter(
    selectFirstItemIfNotExist: Boolean = false,
    selectionChangeListener: ((selectedItem: IconColorUIModel?) -> Unit)? = null
): RadioAdapter<IconColorUIModel, IconColorRadioHolder>(selectFirstItemIfNotExist, selectionChangeListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconColorRadioHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_radio_icon_color, parent, false)

        if (itemView is ImageView)
            return IconColorRadioHolder(itemView)
        else
            throw IllegalStateException()
    }
}