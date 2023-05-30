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

    @Query(
        """
        WITH operation_members AS (
            SELECT 
            main.id as id,
            main.name as name,
            icon.resource_name as icon_resource_name,
            icon_color.value as icon_color_value,
            main.type as type
            FROM (
                SELECT
                    id, 
                    name,
                    icon_id,
                    icon_color_id,
                    0 as type
                FROM
                    account
                    
                UNION ALL    
            
                SELECT
                    id,
                    name,
                    icon_id,
                    icon_color_id,
                    1 as type
                FROM
                    operation_category
                    
            ) AS main
            LEFT JOIN icon ON icon.id = main.icon_id 
            LEFT JOIN icon_color ON icon_color.id = main.icon_color_id
        )
        
        SELECT
        operation.id as id,
        operation_type_id,
        target_data.name as target_name,
        target_data.icon_resource_name as target_icon_resource_name,
        target_data.icon_color_value as target_icon_color_value,
        SUM(operation.sum * operation.currency_exchange_rate) as sum,
        CASE
            WHEN (operation.active_currency_id != :defaultCurrency AND currency_exchange_rate.exchange_rate IS NOT NULL) THEN
                operation.sum * currency_exchange_rate.exchange_rate
            ELSE
                0.0
        END as common_currency_sum,
        (operation.sum * operation.currency_exchange_rate) /
        (
            SELECT 
            SUM(sum * currency_exchange_rate) 
            FROM operation
            WHERE operation_type_id = :operationTypeId
        ) as percent,
        currency_data.name as currency_name
        FROM operation
        LEFT JOIN active_currency as currency_data ON currency_data.id = operation.active_currency_id
        LEFT JOIN currency_exchange_rate ON 
            currency_from_id = operation.active_currency_id AND
            currency_to_id = :defaultCurrency
        LEFT JOIN operation_members AS target_data 
            ON (
                    (
                        operation.operation_type_id = 2 AND 
                        operation.operation_category_id = target_data.id AND 
                        target_data.type = 1
                    ) OR
                    (
                        operation.operation_type_id IN (1, 3) AND 
                        operation.to_account_id = target_data.id AND 
                        target_data.type = 0
                    )
                )
        WHERE operation_type_id = :operationTypeId
        GROUP BY operation.from_account_id, operation.to_account_id, operation.operation_category_id
        """
    )
    fun getPieChartViewFlowByOperationTypeId(
        operationTypeId: Int,
        defaultCurrency: Int
    ): Flow<List<OperationPieChartView>>

    @Query(
        """
        SELECT
        id, 
        sum,
        date_time
        FROM
        operation
        WHERE operation_type_id = :operationTypeId
        GROUP BY DATE(date_time)
    """
    )
    fun getLineChartViewFlowByOperationTypeId(operationTypeId: Int): Flow<List<OperationLineChartView>>

}