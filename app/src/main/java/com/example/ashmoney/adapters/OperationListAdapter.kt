package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ashmoney.R
import com.example.ashmoney.holder.OperationListHolder
import com.example.ashmoney.models.ui.OperationListUIModel

class OperationListAdapter(
    onClick: ((item: OperationListUIModel) -> Unit)? = null
) : ClickedListAdapter<OperationListUIModel, OperationListHolder>(onClick) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationListHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_operation_item2, parent, false)

        return OperationListHolder(itemView)
    }
}