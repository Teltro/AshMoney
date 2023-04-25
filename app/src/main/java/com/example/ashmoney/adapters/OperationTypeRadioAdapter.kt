package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.ashmoney.R
import com.example.ashmoney.holder.OperationTypeHolder
import com.example.ashmoney.models.ui.OperationTypeUIModel

class OperationTypeRadioAdapter(
    selectFirstItemIfNotExist: Boolean = false,
    selectionChangeListener: ((selectedItem: OperationTypeUIModel?) -> Unit)? = null
): RadioAdapter<OperationTypeUIModel, OperationTypeHolder>(
    selectFirstItemIfNotExist,
    selectionChangeListener
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationTypeHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_radio_text, parent, false)

        if (itemView is TextView)
            return OperationTypeHolder(itemView)
        else
            throw IllegalStateException()
    }

}