package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.ashmoney.R
import com.example.ashmoney.holder.OperationCategoryRadioHolder
import com.example.ashmoney.models.ui.OperationCategoryUIModel

class OperationCategoryRadioAdapter(
    selectFirstItemIfNotExist: Boolean = false,
    selectionChangeListener: ((selectedItem: OperationCategoryUIModel?) -> Unit)? = null
) : RadioAdapter<OperationCategoryUIModel, OperationCategoryRadioHolder>(
    selectFirstItemIfNotExist,
    selectionChangeListener
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OperationCategoryRadioHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_radio_icon, parent, false)

        if (itemView is ImageView)
            return OperationCategoryRadioHolder(itemView)
        else
            throw IllegalStateException()
    }
}