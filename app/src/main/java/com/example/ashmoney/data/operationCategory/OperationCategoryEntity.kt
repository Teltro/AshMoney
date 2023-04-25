package com.example.ashmoney.data.operationCategory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operation_category")
data class OperationCategoryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "operation_type_id")
    val operationTypeId: Int,

    @ColumnInfo(name = "icon_id")
    val iconId: Int,

    @ColumnInfo(name = "icon_color_id")
    val iconColorId: Int
)