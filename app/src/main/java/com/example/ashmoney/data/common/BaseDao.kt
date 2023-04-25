package com.example.ashmoney.data.common

import androidx.room.*
import com.example.ashmoney.data.activeCurrency.ActiveCurrencyEntity
import com.example.ashmoney.data.iconColor.IconColorEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface BaseDao<Entity> {

    @Insert
    suspend fun insert(vararg entity: Entity)

    @Update
    suspend fun update(vararg entity: Entity)

    @Delete
    suspend fun delete(vararg entity: Entity)

}