package com.example.ashmoney.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.ashmoney.databinding.RecyclerviewOperationPieChartItemBinding
import com.example.ashmoney.models.ui.OperationPieChartUIModel
import com.example.ashmoney.utils.dpToPx
import com.example.ashmoney.utils.fillImageViewWithIcon
import com.example.ashmoney.utils.round100
import com.example.ashmoney.utils.toPercentString

class OperationPieChartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        private val selectionCircleStrokeWidth by lazy {
            dpToPx(3) // to const
        }
    }

    lateinit var item: OperationPieChartUIModel
        private set

    private val binding = RecyclerviewOperationPieChartItemBinding.bind(itemView)

    fun bind(item: OperationPieChartUIModel) {
        this.item = item

        with(binding) {
            recyclerViewPieChartOperationItemNameTextView.text = item.targetName
            recyclerViewPieChartOperationItemImageView.fillImageViewWithIcon(item.targetIconResourceName, item.targetIconColorValue)
            recyclerViewPieChartOperationItemPercentTextView.text = item.percent.toPercentString()
            recyclerViewPieChartOperationItemSumTextView.text = item.sum.round100().toString()
            recyclerViewPieChartOperationItemCurrencyTextView.text = item.currencyName
        }
    }

}