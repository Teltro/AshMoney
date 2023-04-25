package com.example.ashmoney.holder

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.ashmoney.R
import com.example.ashmoney.databinding.RecyclerviewAccountItemBinding
import com.example.ashmoney.models.ui.AccountUIModel
import com.example.ashmoney.utils.dpToPx
import com.example.ashmoney.utils.getDrawable

class AccountListHolder(private val itemView: View) : ClickedListHolder<AccountUIModel>(itemView) {

    companion object {
        private val selectionCircleStrokeWidth by lazy {
            dpToPx(3) // to const
        }
    }

    private val iconImageView: ImageView
    private val nameTextView: TextView
    private val sumTextView: TextView
    private val currencyTextView: TextView

    init {
        val binding = RecyclerviewAccountItemBinding.bind(itemView)
        iconImageView = binding.recyclerAccountItemIconImageView
        nameTextView = binding.recyclerAccountItemNameTextView
        sumTextView = binding.recyclerAccountItemSumTextView
        currencyTextView = binding.recyclerAccountItemCurrencyTextView
    }

    override lateinit var item: AccountUIModel
        private set

    override fun bind(item: AccountUIModel, onClick: (() -> Unit)?) {
        this.item = item

        val layerDrawable = iconImageView.background as LayerDrawable
        val circleDrawable =
            layerDrawable.findDrawableByLayerId(R.id.radio_icon_circle_selection_circle) as GradientDrawable

        val iconDrawable = iconImageView.context.getDrawable(item.iconResourceName) as Drawable
        iconImageView.setImageDrawable(iconDrawable)

        val color = Color.parseColor(item.iconColorValue)
        circleDrawable.setStroke(selectionCircleStrokeWidth, color)
        iconDrawable.setTint(color)

        nameTextView.text = item.name
        sumTextView.text = item.amountValue.toString()
        currencyTextView.text = item.activeCurrencyName

        itemView.setOnClickListener {
            onClick?.invoke()
        }
    }
}