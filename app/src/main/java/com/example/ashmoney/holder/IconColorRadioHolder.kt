package com.example.ashmoney.holder

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import androidx.core.content.ContextCompat
import com.example.ashmoney.R
import com.example.ashmoney.models.ui.IconColorUIModel

class IconColorRadioHolder(private val imageView: View) : RadioHolder<IconColorUIModel>(imageView) {

    private lateinit var selectionCircleDrawable: Drawable

    override lateinit var item: IconColorUIModel
        private set

    override var selected: Boolean = false
        set(value) {
            selectionCircleDrawable.alpha = if (value) 255 else 0
            field = value
        }

    override fun bind(item: IconColorUIModel, selected: Boolean, onClick: (() -> Unit)?) {
        this.item = item;

        val layerDrawable = imageView.background as LayerDrawable
        val backgroundCircleDrawable =
            layerDrawable.findDrawableByLayerId(R.id.color_circle_background) as GradientDrawable
        selectionCircleDrawable =
            layerDrawable.findDrawableByLayerId(R.id.color_circle_selection_circle)

        this.selected = selected

        //with(imageView.context) {
            /*val colorId = resources.getIdentifier(item.value, "color", packageName)
            val color = ContextCompat.getColor(this, colorId)*/
            backgroundCircleDrawable.setColor(Color.parseColor(item.value))
        //}

        imageView.setOnClickListener {
            onClick?.invoke()
        }
    }

}