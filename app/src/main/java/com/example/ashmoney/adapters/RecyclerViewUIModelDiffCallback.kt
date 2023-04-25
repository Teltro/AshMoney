package com.example.ashmoney.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.ashmoney.models.ui.RecyclerViewUIModel

class RecyclerViewUIModelDiffCallback<Item : RecyclerViewUIModel>() : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.same(newItem)
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}