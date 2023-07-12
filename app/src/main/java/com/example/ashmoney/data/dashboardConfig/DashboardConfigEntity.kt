package com.example.ashmoney.data.dashboardConfig

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ashmoney.data.converters.Converters

@Entity(tableName = "dashboard_config")
data class DashboardConfigEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "accounting_type")
    @TypeConverters(Converters::class)
    val accountingType: DashboardAccountingType,

    @ColumnInfo(name = "accounting_accounts_list")
    @TypeConverters(Converters::class)
    val accountingAccountsList: List<Int>

)