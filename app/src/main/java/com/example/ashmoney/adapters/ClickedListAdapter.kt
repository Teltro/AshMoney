package com.example.ashmoney.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.ashmoney.holder.ClickedListHolder
import com.example.ashmoney.models.ui.RecyclerViewUIModel

abstract class ClickedListAdapter<Item: RecyclerViewUIModel, ViewHolder: ClickedListHolder<Item>>(
    private val onClick: ((clickedItem: Item) -> Unit)? = null
) : ListAdapter<Item, ViewHolder>(RecyclerViewUIModelDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]

        holder.bind(item) {
            onClick?.invoke(item)
        }
    }
}