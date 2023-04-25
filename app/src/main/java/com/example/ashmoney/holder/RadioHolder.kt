package com.example.ashmoney.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.models.ui.RecyclerViewUIModel

abstract class RadioHolder<Item: RecyclerViewUIModel>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract val item: Item
    abstract var selected: Boolean

    abstract fun bind(
        item: Item,
        selected: Boolean = false,
        onClick: (() -> Unit)? = null
    )

}