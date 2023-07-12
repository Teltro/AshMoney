package com.example.ashmoney.data.dashboardConfig

import androidx.room.Query
import com.example.ashmoney.data.common.BaseDao
import kotlinx.coroutines.flow.Flow

interface DashboardConfigDao : BaseDao<DashboardConfigEntity> {

    @Query("SELECT * FROM dashboard_config LIMIT 1")
    fun getSingleFlow(): Flow<DashboardConfigEntity>

}