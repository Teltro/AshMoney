package com.example.ashmoney.data.operation

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.example.ashmoney.models.ui.OperationListUIModel

@DatabaseView(
    """
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
            operation.id as id, 
            operation.name as name,
            operation.operation_type_id as operation_type_id,
            from_data.name as from_name, 
            from_data.icon_resource_name as from_icon_resource_name, 
            from_data.icon_color_value as from_icon_color_value,
            to_data.name as to_name,
            to_data.icon_resource_name as to_icon_resource_name, 
            to_data.icon_color_value as to_icon_color_value,
            operation.sum as sum,
            operation.note as note,
            currency_data.name as currency_name
        FROM operation
        LEFT JOIN active_currency as currency_data ON currency_data.id = operation.active_currency_id
        LEFT JOIN operation_members AS from_data 
            ON (
                    (
                        operation.operation_type_id = 1 AND 
                        operation.operation_category_id = from_data.id AND 
                        from_data.is_operation_category = 1
                    ) OR
                    (
                        operation.operation_type_id IN (2, 3) AND 
                        operation.from_account_id = from_data.id AND 
                        from_data.is_account = 1
                    )
                )
        LEFT JOIN operation_members AS to_data 
            ON (
                    (
                        operation.operation_type_id = 2 AND 
                        operation.operation_category_id = to_data.id AND 
                        to_data.is_operation_category = 1
                    ) OR
                    (
                        operation.operation_type_id IN (1, 3) AND 
                        operation.to_account_id = to_data.id AND 
                        to_data.is_account = 1
                    )
                )
    """
)
data class OperationView(
    @ColumnInfo(name = "id")
    override val id: Int,
    @ColumnInfo(name = "name")
    override val name: String?,
    @ColumnInfo(name = "operation_type_id")
    override val operationTypeId: Int,
    @ColumnInfo(name = "from_name")
    override val fromName: String,
    @ColumnInfo(name = "from_icon_resource_name")
    override val fromIconResourceName: String,
    @ColumnInfo(name = "from_icon_color_value")
    override val fromIconColorValue: String,
    @ColumnInfo(name = "to_name")
    override val toName: String,
    @ColumnInfo(name = "to_icon_resource_name")
    override val toIconResourceName: String,
    @ColumnInfo(name = "to_icon_color_value")
    override val toIconColorValue: String,
    @ColumnInfo(name = "sum")
    override val sum: Double,
    @ColumnInfo(name = "currency_name")
    override val currencyName: String
): OperationListUIModel
