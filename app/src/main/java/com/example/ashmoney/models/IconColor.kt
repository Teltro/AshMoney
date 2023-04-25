package com.example.ashmoney.models

import com.example.ashmoney.models.ui.IconColorUIModel

class IconColor(override val value: String): IconColorUIModel {

    companion object {
        var enumerator: Int = 1;
    }

    override val id: Int = enumerator++;

    override fun equals(other: Any?): Boolean {
        return other is IconColor && value == other.value
    }

}
