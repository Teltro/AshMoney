package com.example.ashmoney.holder

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.ashmoney.R
import com.example.ashmoney.models.ui.OperationCategoryUIModel
import com.example.ashmoney.utils.dpToPx
import com.example.ashmoney.utils.getDrawable

class OperationCategoryRadioHolder(
    private val imageView: ImageView
) : RadioHolder<OperationCategoryUIModel>(imageView) {

    companion object {
        private val selectionCircleStrokeWidth by lazy {
            dpToPx(3) // to const
        }
    }

    private lateinit var iconDrawable: Drawable
    private lateinit var selectionCircleDrawable: GradientDrawable

    override lateinit var item: OperationCategoryUIModel
        private set

    override var selected: Boolean = false
        set(value) {
            selectionCircleDrawable.alpha = if (value) 255 else 0
            field = value
        }

    override fun bind(item: OperationCategoryUIModel, selected: Boolean, onClick: (() -> Unit)?) {
        this.item = item

        val layerDrawable = imageView.background as LayerDrawable
        selectionCircleDrawable =
            layerDrawable.findDrawableByLayerId(R.id.radio_icon_circle_selection_circle) as GradientDrawable

        iconDrawable = imageView.context.getDrawable(item.iconResourceName) as Drawable // ??
        imageView.setImageDrawable(iconDrawable)

        with(imageView.context) {
            /*val colorId = resources.getIdentifier(item.iconColorValue, "color", packageName)
            val color = ContextCompat.getColor(this, colorId)*/
            val color = Color.parseColor(item.iconColorValue)
            selectionCircleDrawable.setStroke(selectionCircleStrokeWidth, color)
            iconDrawable.setTint(color)
        }


        this.selected = selected

        imageView.setOnClickListener {
            onClick?.invoke()
        }
    }

}