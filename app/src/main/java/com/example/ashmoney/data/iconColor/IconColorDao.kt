package com.example.ashmoney.data.iconColor

import androidx.room.*
import com.example.ashmoney.data.activeCurrency.ActiveCurrencyEntity
import com.example.ashmoney.data.common.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface IconColorDao: BaseDao<IconColorEntity> {

    @Query("SELECT * FROM icon_color")
    suspend fun getAll(): List<IconColorEntity>

    @Query("SELECT * FROM icon_color")
    fun getAllFlow(): Flow<List<IconColorEntity>>

}