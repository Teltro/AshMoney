/*
package com.example.ashmoney.data.iconColor

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.IconColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object IconColorRepository {

    private val _list: MutableStateFlow<List<IconColorEntity>> by lazy {
        MutableStateFlow(
            getIconColorListFormResources()
        )
    }

    val list: StateFlow<List<IconColorEntity>> = _list

    fun getByName(name: String): IconColorEntity? {
        return _list.value?.find { item -> item.value == name }
    }


    private fun getIconColorListFormResources(): List<IconColorEntity> {
        val list = mutableListOf<IconColorEntity>()
        var i = 1
        while (true) {
            val name = "iconColor$i"
            val id = getColorIdFromResources(name)
            //val value = getColorByIdFromResources(id)
            if (id != 0)
                list.add(IconColorEntity(name))
            else
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

}*/
