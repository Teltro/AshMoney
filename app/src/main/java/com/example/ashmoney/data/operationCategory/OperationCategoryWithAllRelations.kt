package com.example.ashmoney.data.operationCategory

import androidx.room.Embedded
import androidx.room.Relation
import com.example.ashmoney.data.icon.IconEntity
import com.example.ashmoney.data.iconColor.IconColorEntity
import com.example.ashmoney.models.OperationType
import com.example.ashmoney.models.ui.OperationCategoryUIModel

data class OperationCategoryWithAllRelations(
    @Embedded
    val operationCategory: OperationCategoryEntity,

    @Relation(parentColumn = "icon_id", entityColumn = "id")
    val icon: IconEntity,

    @Relation(parentColumn = "icon_color_id", entityColumn = "id")
    val iconColor: IconColorEntity,
) : OperationCategoryUIModel {

    override val id: Int
        get() = operationCategory.id

    override val name: String
        get() = operationCategory.name

    override val iconResourceName: String
        get() = icon.resourceName

    override val iconColorValue: String
        get() = iconColor.value

}