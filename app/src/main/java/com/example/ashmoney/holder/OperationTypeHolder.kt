package com.example.ashmoney.holder

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import com.example.ashmoney.models.ui.OperationTypeUIModel

class OperationTypeHolder(
    private val textView: TextView
): RadioHolder<OperationTypeUIModel>(textView) {

    private lateinit var backgroundDrawable: GradientDrawable

    override lateinit var item: OperationTypeUIModel
        private set

    override var selected: Boolean = false
        set(value) {
            if (value)
                setSelectedBackground()
            else
                setDefaultBackground()

            field = value
        }

    override fun bind(item: OperationTypeUIModel, selected: Boolean, onClick: (() -> Unit)?) {
        this.item = item

        backgroundDrawable = textView.background as GradientDrawable

        this.selected = selected

        textView.text = item.name
        backgroundDrawable.setStroke(2, Color.RED)

        textView.setOnClickListener {
            onClick?.invoke()
        }
    }

    private fun setSelectedBackground() {
        backgroundDrawable.setColor(Color.RED)
    }

    private fun setDefaultBackground(){
        backgroundDrawable.setColor(Color.TRANSPARENT)
    }
}