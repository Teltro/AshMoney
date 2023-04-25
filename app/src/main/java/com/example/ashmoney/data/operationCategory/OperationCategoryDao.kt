package com.example.ashmoney.data.operationCategory

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.ashmoney.data.common.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationCategoryDao : BaseDao<OperationCategoryEntity> {

    @Query("SELECT * FROM operation_category")
    fun getAll(): List<OperationCategoryEntity>

    @Query("SELECT * FROM operation_category")
    fun getAllFlow(): Flow<List<OperationCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM operation_category")
    fun getAllWithAllRelationsFlow(): Flow<List<OperationCategoryWithAllRelations>>

    @Query("SELECT * FROM operation_category WHERE operation_type_id = :operationTypeId")
    suspend fun getByOperationType(operationTypeId: Int): List<OperationCategoryEntity>

    @Query("SELECT * FROM operation_category WHERE operation_type_id = :operationTypeId")
    suspend fun getWithAllRelationsByOperationType(operationTypeId: Int): List<OperationCategoryWithAllRelations>

    @Query("SELECT * FROM operation_category WHERE operation_type_id = :operationTypeId")
    fun getWithAllRelationsFlowByOperationType(operationTypeId: Int): Flow<List<OperationCategoryWithAllRelations>>
}