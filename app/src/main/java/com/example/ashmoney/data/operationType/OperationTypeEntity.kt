package com.example.ashmoney.data.operationType

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ashmoney.models.ui.OperationTypeUIModel

@Entity(tableName = "operation_type")
data class OperationTypeEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Int = 0,

    @ColumnInfo(name = "name")
    override val name: String

) : OperationTypeUIModel
