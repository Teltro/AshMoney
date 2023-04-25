package com.example.ashmoney.data.account

import androidx.room.Embedded
import androidx.room.Relation
import com.example.ashmoney.data.activeCurrency.ActiveCurrencyEntity
import com.example.ashmoney.data.icon.IconEntity
import com.example.ashmoney.data.iconColor.IconColorEntity
import com.example.ashmoney.models.ui.AccountUIModel


data class AccountWithAllRelations(

    @Embedded
    val account: AccountEntity,

    @Relation(parentColumn = "active_currency_id", entityColumn = "id")
    val activeCurrency: ActiveCurrencyEntity,

    @Relation(parentColumn = "icon_id", entityColumn = "id")
    val icon: IconEntity,

    @Relation(parentColumn = "icon_color_id", entityColumn = "id")
    val iconColor: IconColorEntity,

) : AccountUIModel {

    override val id: Int
        get() = account.id

    override val name: String
        get() = account.name

    override val amountValue: Double
        get() = account.amountValue

    override val activeCurrencyName: String
        get() = activeCurrency.name

    override val iconResourceName: String
        get() = icon.resourceName

    override val iconColorValue: String
        get() = iconColor.value
}
