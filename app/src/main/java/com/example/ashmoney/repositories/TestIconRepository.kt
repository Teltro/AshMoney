package com.example.ashmoney.repositories

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ashmoney.R
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.models.Icon

object TestIconRepository {

    val list: LiveData<List<Icon>> by lazy {
        MutableLiveData<List<Icon>>(
            listOf(
                Icon("SHOP", getResName(R.drawable.ic_outline_shopping_cart_24)),
                Icon("BITCOIN", getResName(R.drawable.ic_baseline_currency_bitcoin_24)),
                Icon("WORK", getResName(R.drawable.ic_outline_work_outline_24)),
                Icon("GIFT", getResName(R.drawable.ic_outline_card_giftcard_24)),
                Icon("MONEY", getResName(R.drawable.ic_outline_money_24)),
                Icon("CARD", getResName(R.drawable.ic_outline_credit_card_24)),
                Icon("FOOD", getResName(R.drawable.ic_outline_fastfood_24)),
                Icon("BUS", getResName(R.drawable.ic_outline_directions_bus_24)),
                Icon("SAVING", getResName(R.drawable.ic_outline_savings_24)),
                Icon("BANK", getResName(R.drawable.ic_outline_account_balance_24))
            )
        )
    }

    fun get(id: Int): Icon? = list.value?.find { icon -> icon.id == id }


    private fun getResName(resId: Int) : String {
        return MainApp.instance.resources.getResourceName(resId)
    }

}