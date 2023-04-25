package com.example.ashmoney.data.operationType

import androidx.room.Dao
import androidx.room.Query
import com.example.ashmoney.data.common.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationTypeDao : BaseDao<OperationTypeEntity> {

    @Query("SELECT * FROM operation_type")
    suspend fun getAll(): List<OperationTypeEntity>

    @Query("SELECT * FROM operation_type")
    fun getAllFlow(): Flow<List<OperationTypeEntity>>

}