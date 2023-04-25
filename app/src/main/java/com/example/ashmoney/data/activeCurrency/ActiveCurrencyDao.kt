package com.example.ashmoney.data.activeCurrency

import androidx.room.*
import com.example.ashmoney.data.common.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveCurrencyDao : BaseDao<ActiveCurrencyEntity> {

    @Query("SELECT * FROM active_currency where id = :currencyId")
    suspend fun getById(currencyId: Int): ActiveCurrencyEntity

    @Query("SELECT * FROM active_currency")
    suspend fun getAll(): List<ActiveCurrencyEntity>

    @Query("SELECT * FROM active_currency")
    fun getAllFlow(): Flow<List<ActiveCurrencyEntity>>

}