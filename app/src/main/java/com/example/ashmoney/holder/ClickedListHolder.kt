package com.example.ashmoney.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.models.ui.RecyclerViewUIModel

abstract class ClickedListHolder<Item: RecyclerViewUIModel>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract val item: Item

    abstract fun bind(
        item: Item,
        onClick: (() -> Unit)?
    )

}