package com.example.ashmoney.data.account

import androidx.room.ColumnInfo

data class AccountListView(
    @ColumnInfo(name = "account_id")
    val accountId: Int? = null,

    @ColumnInfo(name = "amount_value")
    var amountValue: Double = 0.0,

    @ColumnInfo(name = "currency_name")
    var currency: String,

    @ColumnInfo(name = "icon_resource_name")
    var iconResourceName: String,

    @ColumnInfo(name = "icon_color_name")
    var iconColorValue: String

)