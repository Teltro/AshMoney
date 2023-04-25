package com.example.ashmoney.data.currencyExchageRate

import androidx.room.Dao
import androidx.room.Query
import com.example.ashmoney.data.common.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyExchangeRateDao : BaseDao<CurrencyExchangeRateEntity> {

    @Query("""
        SELECT
        active_currency.id as id,
        active_currency.name as currency_name,
        rate_data.exchange_rate AS rate
        FROM active_currency
        LEFT JOIN (
            SELECT 
            currency_to_id,
            exchange_rate 
            FROM
            currency_exchange_rate
            WHERE currency_from_id = :currencyId
        ) AS rate_data ON active_currency.id = rate_data.currency_to_id 
        WHERE active_currency.id != :currencyId
    """)
    fun getAllEntityViewFlowByCurrencyId(currencyId: Int): Flow<List<CurrencyExchangeRateView>>

    @Query("SELECT exchange_rate FROM currency_exchange_rate WHERE currency_from_id = :currencyId")
    suspend fun getCurrencyExchangeRateValueByCurrencyId(currencyId: Int): Double?

    @Query("SELECT exchange_rate FROM currency_exchange_rate WHERE currency_from_id = :currencyFromId AND currency_to_id = :currencyToId")
    suspend fun getCurrencyExchangeRateValueByCurrencyId(currencyFromId: Int, currencyToId: Int): Double?

}