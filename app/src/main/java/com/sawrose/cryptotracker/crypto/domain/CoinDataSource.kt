package com.sawrose.cryptotracker.crypto.domain

import com.sawrose.cryptotracker.core.domain.util.NetworkError
import com.sawrose.cryptotracker.core.domain.util.Result
import java.time.ZonedDateTime

interface CoinDataSource {
    suspend fun getCoins(): Result<List<Coin>, NetworkError>

    suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        endRange: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError>
}