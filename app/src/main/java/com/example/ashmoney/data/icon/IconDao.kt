package com.example.ashmoney.data.icon

import androidx.room.Dao
import androidx.room.Query
import com.example.ashmoney.data.common.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface IconDao : BaseDao<IconEntity> {

    @Query("SELECT * FROM icon")
    suspend fun getAll(): List<IconEntity>

    @Query("SELECT * FROM icon")
    fun getAllFlow(): Flow<List<IconEntity>>

}