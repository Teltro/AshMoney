package com.example.ashmoney.holder

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.ImageView
import com.example.ashmoney.R
import com.example.ashmoney.databinding.RecyclerviewOperationItem2Binding
import com.example.ashmoney.models.ui.OperationListUIModel
import com.example.ashmoney.utils.OperationTypeId
import com.example.ashmoney.utils.dpToPx
import com.example.ashmoney.utils.getDrawable

class OperationListHolder(val itemView: View) : ClickedListHolder<OperationListUIModel>(itemView) {

    companion object {
        private val circleStrokeWidth by lazy {
            dpToPx(3) // to const
        }
    }

    override lateinit var item: OperationListUIModel
        private set

    private val binding = RecyclerviewOperationItem2Binding.bind(itemView)

    override fun bind(item: OperationListUIModel, onClick: (() -> Unit)?) {
        this.item = item

        val operationTypeId = OperationTypeId.fromId(item.operationTypeId)

        with(binding) {
            operationTypeId?.let {
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

    private fun getBackgroundColorIdByOperationId(operationTypeId: OperationTypeId): Int {
        return when (operationTypeId) {
            OperationTypeId.INCOME -> R.color.incomeBackgroundColor
            OperationTypeId.EXPENSE -> R.color.expenseBackgroundColor
            OperationTypeId.TRANSFER -> R.color.transferBackgroundColor
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