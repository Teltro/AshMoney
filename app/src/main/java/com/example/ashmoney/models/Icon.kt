package com.example.ashmoney.models

import com.example.ashmoney.models.ui.IconUIModel

class Icon(
    override val name: String,
    override val resourceName: String,
) : IconUIModel {

    companion object {
        var enumerator: Int = 1;
    }

    override val id: Int = enumerator++;

    override fun equals(other: Any?): Boolean {
        return other is Icon &&
        other.id == id &&
        other.resourceName == resourceName
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + resourceName.hashCode()
        result = 31 * result + id
        return result
    }

}