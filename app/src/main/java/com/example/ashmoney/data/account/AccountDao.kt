package com.example.ashmoney.data.account

import androidx.room.*
import com.example.ashmoney.data.common.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao : BaseDao<AccountEntity> {

    @Query("SELECT * FROM account WHERE id = :id")
    suspend fun getById(id: Int): AccountEntity?

    @Query("DELETE FROM account WHERE id IN (:id)")
    suspend fun deleteById(vararg id: Int)

    @Transaction
    @Query("SELECT * FROM account WHERE id = :id")
    suspend fun getWithAllRelationsById(id: Int): AccountWithAllRelations?

    @Query("SELECT * FROM account")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM account")
    fun getAllFlow(): Flow<List<AccountEntity>>

    @Transaction
    @Query("SELECT * FROM account")
    fun getAllWithAllRelationsFlow(): Flow<List<AccountWithAllRelations>>


}