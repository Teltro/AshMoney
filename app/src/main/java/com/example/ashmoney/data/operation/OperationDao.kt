package com.example.ashmoney.data.operation

import androidx.room.Dao
import androidx.room.Query
import com.example.ashmoney.data.common.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao : BaseDao<OperationEntity> {

    @Query("SELECT * FROM operation WHERE id = :operationId")
    suspend fun getById(operationId: Int): OperationEntity?

    @Query("SELECT * FROM operation WHERE id = :operationId")
    suspend fun getWithAllRelationsById(operationId: Int): OperationWithAllRelations?

    @Query("DELETE FROM operation WHERE id IN (:operationId)")
    suspend fun deleteById(vararg operationId: Int)

    @Query("SELECT * FROM OperationView")
    suspend fun getAllViewEntity(): List<OperationView>

    @Query("SELECT * FROM OperationView")
    fun getAllViewEntityFlow(): Flow<List<OperationView>>

}