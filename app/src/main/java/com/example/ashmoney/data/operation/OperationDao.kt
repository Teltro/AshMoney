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

    @Query("SELECT * FROM OperationListView")
    suspend fun getAllViewEntity(): List<OperationListView>

    @Query("SELECT * FROM OperationListView")
    fun getAllViewEntityFlow(): Flow<List<OperationListView>>

/*    @Query("SELECT * FROM OperationListView WHERE operation_type_id = :operationTypeId")
    fun getAllViewEntityFlowByOperationTypeId(operationTypeId: Int): Flow<List<OperationListView>>*/

    @Query("""
        WITH operation_members AS (
            SELECT 
            main.id as id,
            main.name as name,
            icon.resource_name as icon_resource_name,
            icon_color.value as icon_color_value,
            main.is_account,
            main.is_operation_category
            FROM (
                SELECT
                    id, 
                    name,
                    icon_id,
                    icon_color_id,
                    1 as is_account,
                    0 as is_operation_category
                FROM
                    account
                    
                UNION ALL    
            
                SELECT
                    id,
                    name,
                    icon_id,
                    icon_color_id,
                    0 as is_account,
                    1 as is_operation_category
                FROM
                    operation_category
                    
            ) AS main
            LEFT JOIN icon ON icon.id = main.icon_id 
            LEFT JOIN icon_color ON icon_color.id = main.icon_color_id
        )
        
        SELECT
        id,
        name,
        operation_type_id,
        target_data.name as target_name,
        target_data.icon_resource_name as target_icon_resource_name,
        target_data.icon_color_value as target_icon_color_value,
        sum,    
        sum / SUM(sum) as percent,
        currency_data.name as currency_name
        FROM operation
        LEFT JOIN active_currency as currency_data ON currency_data.id = operation.active_currency_id
        LEFT JOIN operation_members AS target_data 
            ON (
                    (
                        operation.operation_type_id = 1 AND 
                        operation.operation_category_id = target_data.id AND 
                        target_data.is_operation_category = 1
                    ) OR
                    (
                        operation.operation_type_id IN (2, 3) AND 
                        operation.from_account_id = target_data.id AND 
                        target_data.is_account = 1
                    )
                )
        WHERE operation_type_id = :operationTypeId
        GROUP BY id
        """)
    fun getAllViewEntityFlowByOperationTypeId(operationTypeId: Int): Flow<List<OperationPieChartView>>

}