package com.example.ashmoney.data.operation

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import com.example.ashmoney.data.account.AccountEntity
import com.example.ashmoney.data.account.AccountWithAllRelations
import com.example.ashmoney.data.activeCurrency.ActiveCurrencyEntity
import com.example.ashmoney.data.icon.IconEntity
import com.example.ashmoney.data.iconColor.IconColorEntity
import com.example.ashmoney.data.operationCategory.OperationCategoryEntity
import com.example.ashmoney.data.operationCategory.OperationCategoryWithAllRelations
import com.example.ashmoney.data.operationType.OperationTypeEntity

data class OperationWithAllRelations(
    @Embedded
    val operation: OperationEntity,

    @Relation(parentColumn = "operation_type_id", entityColumn = "id")
    val operationType: OperationTypeEntity,

    @Relation(parentColumn = "from_account_id", entityColumn = "id", entity = AccountEntity::class)
    val fromAccount: AccountWithAllRelations?,

    @Relation(parentColumn = "to_account_id", entityColumn = "id", entity = AccountEntity::class)
    val toAccount: AccountWithAllRelations?,

    @Relation(parentColumn = "operation_category_id", entityColumn = "id", entity = OperationCategoryEntity::class)
    val operationCategory: OperationCategoryWithAllRelations?,

    @Relation(parentColumn = "active_currency_id", entityColumn = "id")
    val activeCurrency: ActiveCurrencyEntity,

    )
