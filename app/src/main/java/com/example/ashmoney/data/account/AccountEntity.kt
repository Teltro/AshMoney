package com.example.ashmoney.data.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ashmoney.models.ui.AccountUIModel

@Entity(tableName = "account")
class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "amount_value")
    var amountValue: Double = 0.0,

    @ColumnInfo(name = "active_currency_id")
    var activeCurrencyId: Int,

    @ColumnInfo(name = "icon_id")
    var iconId: Int,

    @ColumnInfo(name = "icon_color_id")
    var iconColorId: Int
) {
}