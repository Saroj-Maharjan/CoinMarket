package com.sawrose.cryptotracker.crypto.domain

import com.sawrose.cryptotracker.core.domain.util.NetworkError
import com.sawrose.cryptotracker.core.domain.util.Result

interface CoinDataSource {
    suspend fun getCoins(): Result<List<Coin>, NetworkError>
}