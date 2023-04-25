package com.example.ashmoney.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ashmoney.core.MainApp
import com.example.ashmoney.data.account.AccountDao
import com.example.ashmoney.data.account.AccountEntity
import com.example.ashmoney.data.activeCurrency.ActiveCurrencyDao
import com.example.ashmoney.data.activeCurrency.ActiveCurrencyEntity
import com.example.ashmoney.data.currencyExchageRate.CurrencyExchangeRateDao
import com.example.ashmoney.data.currencyExchageRate.CurrencyExchangeRateEntity
import com.example.ashmoney.data.icon.IconDao
import com.example.ashmoney.data.icon.IconEntity
import com.example.ashmoney.data.iconColor.IconColorDao
import com.example.ashmoney.data.iconColor.IconColorEntity
import com.example.ashmoney.data.operation.OperationDao
import com.example.ashmoney.data.operation.OperationEntity
import com.example.ashmoney.data.operation.OperationView
import com.example.ashmoney.data.operationCategory.OperationCategoryDao
import com.example.ashmoney.data.operationCategory.OperationCategoryEntity
import com.example.ashmoney.data.operationType.OperationTypeDao
import com.example.ashmoney.data.operationType.OperationTypeEntity

@Database(
    entities = [
        AccountEntity::class,
        ActiveCurrencyEntity::class,
        CurrencyExchangeRateEntity::class,
        IconColorEntity::class,
        IconEntity::class,
        OperationTypeEntity::class,
        OperationCategoryEntity::class,
        OperationEntity::class
    ],
    views = [
        OperationView::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun activeCurrencyDao(): ActiveCurrencyDao
    abstract fun currencyExchangeRateDao(): CurrencyExchangeRateDao
    abstract fun iconColorDao(): IconColorDao
    abstract fun iconDao(): IconDao
    abstract fun operationTypeDao(): OperationTypeDao
    abstract fun operationCategoryDao(): OperationCategoryDao
    abstract fun operationDao(): OperationDao

    companion object {
        val instance by lazy {
            Room.databaseBuilder(
                MainApp.instance.applicationContext,
                AppDatabase::class.java,
                "test-database"
            ).build()
        }
    }

}