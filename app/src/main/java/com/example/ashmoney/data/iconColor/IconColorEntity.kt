package com.example.ashmoney.data.iconColor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ashmoney.models.IconColor
import com.example.ashmoney.models.ui.IconColorUIModel

@Entity(tableName = "icon_color")
data class IconColorEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Int = 0,

    @ColumnInfo(name = "value")
    override val value: String,

    @ColumnInfo(name = "name")
    val name: String?

) : IconColorUIModel
