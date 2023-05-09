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

    @Query("""
        SELECT 
        SUM(amount_value * (
            CASE 
                WHEN exchange.value IS NOT NULL THEN exchange.value 
                ELSE 1.0
            END
        ))
        FROM account
        LEFT JOIN (
            SELECT
            exchange_rate as value,
            currency_from_id
            FROM currency_exchange_rate
            WHERE currency_exchange_rate.currency_to_id = :defaultCurrencyId
        ) AS exchange ON account.active_currency_id = exchange.currency_from_id 
    """)
    /*@Query("""
        SELECT 
        SUM(amount_value) 
        FROM account
        WHERE :defaultCurrencyId = :defaultCurrencyId

    """)*/
    fun getTotalSumFlow(defaultCurrencyId: Int): Flow<Double>


}