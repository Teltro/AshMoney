package com.example.ashmoney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.ashmoney.R
import com.example.ashmoney.holder.OperationPieChartItemViewHolder
import com.example.ashmoney.models.ui.OperationPieChartUIModel

class OperationPieChartItemAdapter
    : ListAdapter<OperationPieChartUIModel, OperationPieChartItemViewHolder>(RecyclerViewUIModelDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OperationPieChartItemViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_operation_pie_chart_item, parent, false)

        return OperationPieChartItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OperationPieChartItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}