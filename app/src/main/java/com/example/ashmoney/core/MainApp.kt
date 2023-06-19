package com.example.ashmoney.core

import android.app.Application
import androidx.room.Room
import com.example.ashmoney.R
import com.example.ashmoney.data.AppDatabase
import com.example.ashmoney.data.account.AccountEntity
import com.example.ashmoney.data.activeCurrency.ActiveCurrencyEntity
import com.example.ashmoney.data.currencyExchageRate.CurrencyExchangeRateEntity
import com.example.ashmoney.data.icon.IconEntity
import com.example.ashmoney.data.iconColor.IconColorEntity
import com.example.ashmoney.data.operation.OperationEntity
import com.example.ashmoney.data.operationCategory.OperationCategoryEntity
import com.example.ashmoney.data.operationType.OperationTypeEntity
import com.example.ashmoney.utils.toIsoString
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.util.*

class MainApp : Application() {

    companion object {
        lateinit var instance: MainApp
    }

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this;
        createDb()
    }

    private fun createDb() {
        applicationContext.deleteDatabase("AshMoneyDb")

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "AshMoneyDb"
        ).build()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            //MainScope().launch(Dispatchers.IO) {
            db.activeCurrencyDao().insert(
                ActiveCurrencyEntity(name = "USD", isoCode = 840),
                ActiveCurrencyEntity(name = "BYN", isoCode = 933)
            )

            db.iconColorDao().insert(
                IconColorEntity(value = "#FFFF0000", name = "iconColor1"),
                IconColorEntity(value = "#FFFF8000", name = "iconColor2"),
                IconColorEntity(value = "#FFFFFF00", name = "iconColor3"),
                IconColorEntity(value = "#FF80FF00", name = "iconColor4"),
                IconColorEntity(value = "#FF00FF00", name = "iconColor5"),
                IconColorEntity(value = "#FF00FF80", name = "iconColor6"),
                IconColorEntity(value = "#FF00FFFF", name = "iconColor7"),
                IconColorEntity(value = "#FF0080FF", name = "iconColor8"),
                IconColorEntity(value = "#FF0000FF", name = "iconColor9"),
                IconColorEntity(value = "#FF8000FF", name = "iconColor10"),
                IconColorEntity(value = "#FFFF00FF", name = "iconColor11"),
                IconColorEntity(value = "#FFFF0080", name = "iconColor12")
            )

            db.iconDao().insert(
                IconEntity(
                    name = "SHOP",
                    resourceName = getResName(R.drawable.ic_outline_shopping_cart_24)
                ),
                IconEntity(
                    name = "BITCOIN",
                    resourceName = getResName(R.drawable.ic_baseline_currency_bitcoin_24)
                ),
                IconEntity(
                    name = "WORK",
                    resourceName = getResName(R.drawable.ic_outline_work_outline_24)
                ),
                IconEntity(
                    name = "GIFT",
                    resourceName = getResName(R.drawable.ic_outline_card_giftcard_24)
                ),
                IconEntity(
                    name = "MONEY",
                    resourceName = getResName(R.drawable.ic_outline_money_24)
                ),
                IconEntity(
                    name = "CARD",
                    resourceName = getResName(R.drawable.ic_outline_credit_card_24)
                ),
                IconEntity(
                    name = "FOOD",
                    resourceName = getResName(R.drawable.ic_outline_fastfood_24)
                ),
                IconEntity(
                    name = "BUS",
                    resourceName = getResName(R.drawable.ic_outline_directions_bus_24)
                ),
                IconEntity(
                    name = "SAVING",
                    resourceName = getResName(R.drawable.ic_outline_savings_24)
                ),
                IconEntity(
                    name = "BANK",
                    resourceName = getResName(R.drawable.ic_outline_account_balance_24)
                )
            )

            val activeCurrencyList = db.activeCurrencyDao().getAll()
            val iconList = db.iconDao().getAll()
            val iconColorList = db.iconColorDao().getAll()

            db.accountDao().insert(
                AccountEntity(
                    name = "Наличные",
                    amountValue = 15.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    iconId = iconList[4].id,
                    iconColorId = iconColorList[0].id
                ),
                AccountEntity(
                    name = "Банк",
                    amountValue = 20.0,
                    activeCurrencyId = activeCurrencyList[0].id,
                    iconId = iconList[9].id,
                    iconColorId = iconColorList[1].id
                ),
                AccountEntity(
                    name = "Копилка",
                    amountValue = 30.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    iconId = iconList[8].id,
                    iconColorId = iconColorList[2].id
                )
            )

            db.operationTypeDao().insert(
                OperationTypeEntity(name = "Доход"),
                OperationTypeEntity(name = "Расход"),
                OperationTypeEntity(name = "Перевод")
            )

            val operationTypeList = db.operationTypeDao().getAll()

            db.operationCategoryDao().insert(
                OperationCategoryEntity(
                    name = "Продукты",
                    operationTypeId = operationTypeList[1].id,
                    iconId = iconList[5].id,
                    iconColorId = iconColorList[0].id
                ),
                OperationCategoryEntity(
                    name = "Налоги",
                    operationTypeId = operationTypeList[1].id,
                    iconId = iconList[9].id,
                    iconColorId = iconColorList[1].id
                ),
                OperationCategoryEntity(
                    name = "Транспорт",
                    operationTypeId = operationTypeList[1].id,
                    iconId = iconList[7].id,
                    iconColorId = iconColorList[2].id
                ),
                OperationCategoryEntity(
                    name = "Работа",
                    operationTypeId = operationTypeList[0].id,
                    iconId = iconList[2].id,
                    iconColorId = iconColorList[3].id
                ),
                OperationCategoryEntity(
                    name = "Акции",
                    operationTypeId = operationTypeList[0].id,
                    iconId = iconList[1].id,
                    iconColorId = iconColorList[4].id
                )
            )

            val accountList = db.accountDao().getAll()
            val operationCategoryList = db.operationCategoryDao().getAll()

            db.operationDao().insert(
                OperationEntity(
                    name = null,
                    periodId = null,
                    operationTypeId = operationTypeList[0].id,
                    fromAccountId = null,
                    toAccountId = accountList[0].id,
                    operationCategoryId = operationCategoryList[3].id,
                    sum = 5.0,
                    exchangeRateCoefficient = 1.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    dateTime = LocalDateTime.now().minusDays(1),
                    note = null
                ),
                OperationEntity(
                    name = null,
                    periodId = null,
                    operationTypeId = operationTypeList[0].id,
                    fromAccountId = null,
                    toAccountId = accountList[0].id,
                    operationCategoryId = operationCategoryList[4].id,
                    sum = 15.0,
                    exchangeRateCoefficient = 1.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    dateTime = LocalDateTime.now(),
                    note = null
                ),
                OperationEntity(
                    name = null,
                    periodId = null,
                    operationTypeId = operationTypeList[0].id,
                    fromAccountId = null,
                    toAccountId = accountList[1].id,
                    operationCategoryId = operationCategoryList[3].id,
                    sum = 2.0,
                    exchangeRateCoefficient = 1.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    dateTime = LocalDateTime.now().plusDays(2),
                    note = null
                ),
                OperationEntity(
                    name = null,
                    periodId = null,
                    operationTypeId = operationTypeList[0].id,
                    fromAccountId = null,
                    toAccountId = accountList[0].id,
                    operationCategoryId = operationCategoryList[2].id,
                    sum = 1.0,
                    exchangeRateCoefficient = 1.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    dateTime = LocalDateTime.now().minusDays(2),
                    note = null
                ),
                OperationEntity(
                    name = null,
                    periodId = null,
                    operationTypeId = operationTypeList[1].id,
                    fromAccountId = accountList[0].id,
                    toAccountId = null,
                    operationCategoryId = operationCategoryList[2].id,
                    sum = 25.0,
                    exchangeRateCoefficient = 1.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    dateTime = LocalDateTime.now().plusDays(1),
                    note = null
                ),
                OperationEntity(
                    name = null,
                    periodId = null,
                    operationTypeId = operationTypeList[1].id,
                    fromAccountId = accountList[0].id,
                    toAccountId = null,
                    operationCategoryId = operationCategoryList[3].id,
                    sum = 13.0,
                    exchangeRateCoefficient = 1.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    dateTime = LocalDateTime.now().minusDays(2),
                    note = null
                ),
                OperationEntity(
                    name = null,
                    periodId = null,
                    operationTypeId = operationTypeList[1].id,
                    fromAccountId = accountList[2].id,
                    toAccountId = null,
                    operationCategoryId = operationCategoryList[0].id,
                    sum = 9.0,
                    exchangeRateCoefficient = 1.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    dateTime = LocalDateTime.now(),
                    note = null
                ),
                OperationEntity(
                    name = null,
                    periodId = null,
                    operationTypeId = operationTypeList[1].id,
                    fromAccountId = accountList[1].id,
                    toAccountId = null,
                    operationCategoryId = operationCategoryList[0].id,
                    sum = 7.0,
                    exchangeRateCoefficient = 1.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    dateTime = LocalDateTime.now().plusDays(2),
                    note = null
                ),
                OperationEntity(
                    name = null,
                    periodId = null,
                    operationTypeId = operationTypeList[2].id,
                    fromAccountId = accountList[2].id,
                    toAccountId = accountList[1].id,
                    operationCategoryId = null,
                    sum = 4.0,
                    exchangeRateCoefficient = 1.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    dateTime = LocalDateTime.now().minusDays(1),
                    note = null
                ),
                OperationEntity(
                    name = null,
                    periodId = null,
                    operationTypeId = operationTypeList[2].id,
                    fromAccountId = accountList[1].id,
                    toAccountId = accountList[2].id,
                    operationCategoryId = null,
                    sum = 4.0,
                    exchangeRateCoefficient = 1.0,
                    activeCurrencyId = activeCurrencyList[1].id,
                    dateTime = LocalDateTime.now().plusDays(1),
                    note = null
                ),
            )

            db.currencyExchangeRateDao().insert(
                CurrencyExchangeRateEntity(
                    currencyFromId = activeCurrencyList[0].id,
                    currencyToId = activeCurrencyList[1].id,
                    exchangeRate = 3.0,
                    dateTime = LocalDateTime.now()
                ),
                CurrencyExchangeRateEntity(
                    currencyFromId = activeCurrencyList[1].id,
                    currencyToId = activeCurrencyList[0].id,
                    exchangeRate = 1 / 3.0,
                    dateTime = LocalDateTime.now()
                )

            )

        }

    }

    private fun getResName(resId: Int): String {
        return MainApp.instance.resources.getResourceName(resId)
    }
}