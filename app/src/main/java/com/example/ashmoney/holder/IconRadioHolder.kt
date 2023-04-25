package com.example.ashmoney.holder

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.ashmoney.R
import com.example.ashmoney.models.ui.IconColorUIModel
import com.example.ashmoney.models.ui.IconUIModel
import com.example.ashmoney.utils.dpToPx
import com.example.ashmoney.utils.getDrawable

class IconRadioHolder(
    private val imageView: ImageView,
    private val getIconColor: (() -> IconColorUIModel?)
) : RadioHolder<IconUIModel>(imageView) {

    companion object {
        private val selectionCircleStrokeWidth by lazy {
            dpToPx(3) // to const
        }
    }

    private lateinit var iconDrawable: Drawable
    private lateinit var selectionCircleDrawable: GradientDrawable

    override lateinit var item: IconUIModel
        private set

    override var selected: Boolean = false
        set(value) {
            if (value) {
                setSelectedBackgroundColor()
            } else {
                setDefaultBackgroundColor()
            }

            field = value
        }

    override fun bind(item: IconUIModel, selected: Boolean, onClick: (() -> Unit)?) {
        this.item = item

        val layerDrawable = imageView.background as LayerDrawable
        selectionCircleDrawable =
            layerDrawable.findDrawableByLayerId(R.id.radio_icon_circle_selection_circle) as GradientDrawable

        iconDrawable = imageView.context.getDrawable(item.resourceName) as Drawable // ??
        imageView.setImageDrawable(iconDrawable)

        this.selected = selected

        imageView.setOnClickListener {
            onClick?.invoke()
        }
    }

    private fun setSelectedBackgroundColor() {
        with(imageView.context) {
            val iconColor = getIconColor.invoke()
            iconColor?.let {
                /*val colorId = resources.getIdentifier(it.value, "color", packageName)
                val color = ContextCompat.getColor(this, colorId)*/
                val color = Color.parseColor(it.value)
                selectionCircleDrawable.setStroke(selectionCircleStrokeWidth, color)
                selectionCircleDrawable.alpha = 255
                iconDrawable.setTint(color)
            } ?: run {
                val color = ContextCompat.getColor(this, R.color.white)
                selectionCircleDrawable.setStroke(selectionCircleStrokeWidth, color)
                selectionCircleDrawable.alpha = 255
                iconDrawable.setTint(color)
            }
        }
    }

    private fun setDefaultBackgroundColor() {
        val color = ContextCompat.getColor(imageView.context, R.color.white)
        selectionCircleDrawable.alpha = 0
        iconDrawable.setTint(color)
    }

    fun updateColor() {
        if (selected)
            setSelectedBackgroundColor()
    }

}