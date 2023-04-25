package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.ashmoney.R
import com.example.ashmoney.holder.IconRadioHolder
import com.example.ashmoney.models.ui.IconColorUIModel
import com.example.ashmoney.models.ui.IconUIModel

class IconRadioAdapter(
    selectFirstItemIfNotExist: Boolean = false,
    selectionChangeListener: ((selectedItem: IconUIModel?) -> Unit)? = null
) : RadioAdapter<IconUIModel, IconRadioHolder>(selectFirstItemIfNotExist, selectionChangeListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconRadioHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_radio_icon, parent, false)

        if (itemView is ImageView)
            return IconRadioHolder(itemView) {
                iconColor
            }
        else
            throw IllegalStateException()
    }

    var iconColor: IconColorUIModel? = null
        set(value) {

            field = value

            selectedViewHolder?.updateColor()
        }

}