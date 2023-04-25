package com.example.ashmoney.data.icon

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ashmoney.models.ui.IconUIModel

@Entity(tableName = "icon")
data class IconEntity (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Int = 0,

    @ColumnInfo(name = "name")
    override val name: String,

    @ColumnInfo(name = "resource_name")
    override val resourceName: String

) : IconUIModel