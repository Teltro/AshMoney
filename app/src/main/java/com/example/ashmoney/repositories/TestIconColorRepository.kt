package com.example.ashmoney.repositories

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.IconColor

object TestIconColorRepository {

    private val _list: MutableLiveData<List<IconColor>> by lazy {
        MutableLiveData(
            getIconColorListFormResources()
        )
    }

    val list: LiveData<out List<IconColor>> = _list

    fun getByName(name: String): IconColor? {
        return _list.value?.find { item -> item.value == name }
    }


    private fun getIconColorListFormResources(): List<IconColor> {
        val list = mutableListOf<IconColor>()
        var i = 1
        while (true) {
            val name = "iconColor$i"
            val id = getColorIdFromResources(name)
            if (id != 0) {
                val hex = MainApp.instance.getString(id)
                list.add(IconColor(hex))
            } else
                break

            i++
        }
        return list
    }

    private fun getColorIdFromResources(name: String): Int {
        return MainApp.instance.run {
            resources.getIdentifier(name, "color", packageName)
        }
    }

    private fun getColorByIdFromResources(id: Int): Int = Color.parseColor(MainApp.instance.getString(id))

}