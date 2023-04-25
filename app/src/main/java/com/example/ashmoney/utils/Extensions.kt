package com.example.ashmoney.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.VectorDrawable
import android.text.BoringLayout
import android.text.Editable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import com.example.ashmoney.core.MainApp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun Context.getDrawable(resName: String): Drawable? =
    AppCompatResources.getDrawable(
        this,
        this.resources.getIdentifier(resName, "drawable", this.packageName)
    )

fun Drawable.overrideColor(_color: Int) {
    when(this) {
        is GradientDrawable -> setColor(_color)
        is ShapeDrawable -> paint.color = _color
        is ColorDrawable -> color = _color
    }
}

fun dpToPx(dp: Number): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), MainApp.instance.resources.displayMetrics).toInt()
}

fun ViewGroup.setEnabledForAll(isEnabled: Boolean) {
    this.isEnabled = isEnabled
    for (child in this.children) {
        if (child !is ViewGroup)
            child.isEnabled = isEnabled
        else
            child.setEnabledForAll(isEnabled)
    }
}

private const val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
private const val DEFAULT_DATE_TIME = "yyyy-MM-dd HH:mm:ss"

fun Date.toIsoString(): String {
    val dateFormat: DateFormat = SimpleDateFormat(ISO)
    return dateFormat.format(this)
}

fun Date.toDefaultString(): String {
    val dateFormat: DateFormat = SimpleDateFormat(DEFAULT_DATE_TIME)
    return dateFormat.format(this)
}

fun String.fromIsoToDate(): Date? {
    val dateFormat: DateFormat = SimpleDateFormat(ISO)
    return dateFormat.parse(this)
}
