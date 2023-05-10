package com.example.ashmoney.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.databinding.RecyclerviewOperationPieChartItemBinding
import com.example.ashmoney.models.ui.OperationPieChartUIModel
import com.example.ashmoney.utils.fillImageViewWithIcon
import com.example.ashmoney.utils.round100

class OperationPieChartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var item: OperationPieChartUIModel
        private set

    private val binding = RecyclerviewOperationPieChartItemBinding.bind(itemView)

    fun bind(item: OperationPieChartUIModel) {
        this.item = item

        with(binding) {
            recyclerViewPieChartOperationItemNameTextView.text = item.name
            recyclerViewPieChartOperationItemImageView.fillImageViewWithIcon(item.targetIconResourceName, item.targetIconColorValue)
            recyclerViewPieChartOperationItemCurrencyTextView.text = item.percent.round100().toString()
            recyclerViewPieChartOperationItemSumTextView.text = item.sum.round100().toString()
            recyclerViewPieChartOperationItemCurrencyTextView.text = item.currencyName
        }
    }

}