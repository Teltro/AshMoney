package com.example.ashmoney.holder

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.ImageView
import com.example.ashmoney.R
import com.example.ashmoney.databinding.RecyclerviewOperationItem2Binding
import com.example.ashmoney.models.ui.OperationListUIModel2
import com.example.ashmoney.utils.OperationType
import com.example.ashmoney.utils.dpToPx
import com.example.ashmoney.utils.getDrawable

class OperationListHolder(val itemView: View) : ClickedListHolder<OperationListUIModel2>(itemView) {

    companion object {
        private val circleStrokeWidth by lazy {
            dpToPx(3) // to const
        }
    }

    override lateinit var item: OperationListUIModel2
        private set

    private val binding = RecyclerviewOperationItem2Binding.bind(itemView)

    override fun bind(item: OperationListUIModel2, onClick: (() -> Unit)?) {
        this.item = item

        val operationType = OperationType.fromId(item.operationTypeId)

        with(binding) {
            operationType?.let {
                root.setBackgroundResource(getBackgroundColorIdByOperationId(it))
            }
            recyclerViewOperationItemFromImageView.fillImageViewWithIcon(item.fromIconResourceName, item.fromIconColorValue)
            recyclerViewOperationItemFromTextView.text = item.fromName
            recyclerViewOperationItemToImageView.fillImageViewWithIcon(item.toIconResourceName, item.toIconColorValue)
            recyclerViewOperationItemToTextView.text = item.toName
            recyclerVIewOperationItemSumTextView.text = item.sum.toString()
            recyclerViewOperationItemCurrencyTextView.text = item.currencyName
        }

        itemView.setOnClickListener {
            onClick?.invoke()
        }
    }

    private fun getBackgroundColorIdByOperationId(operationType: OperationType): Int {
        return when (operationType) {
            OperationType.INCOME -> R.color.incomeBackgroundColor
            OperationType.EXPENSE -> R.color.expenseBackgroundColor
            OperationType.TRANSFER -> R.color.transferBackgroundColor
        }
    }

    private fun ImageView.fillImageViewWithIcon(iconResourceName: String, iconColorValue: String) {
        val layerDrawable = this.background as LayerDrawable
        val circleDrawable =
            layerDrawable.findDrawableByLayerId(R.id.radio_icon_circle_selection_circle) as GradientDrawable

        val iconDrawable = this.context.getDrawable(iconResourceName) as Drawable // ??
        this.setImageDrawable(iconDrawable)

        val color = Color.parseColor(iconColorValue)
        iconDrawable.setTint(color)
        circleDrawable.setStroke(circleStrokeWidth, color)
    }
}