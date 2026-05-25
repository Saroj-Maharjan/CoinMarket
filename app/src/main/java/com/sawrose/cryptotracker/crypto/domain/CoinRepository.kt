package com.sawrose.cryptotracker.crypto.domain

import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.StoreReadResponse
import java.time.ZonedDateTime

interface CoinRepository {
    fun getCoins(): Flow<StoreReadResponse<List<Coin>>>
    fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        endRange: ZonedDateTime
    ): Flow<StoreReadResponse<List<CoinPrice>>>

    suspend fun writeCoins(coins: List<Coin>)
    suspend fun writeCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        endRange: ZonedDateTime,
        history: List<CoinPrice>
    )
}
