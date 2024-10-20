package com.sawrose.cryptotracker.crypto.data.networking

import com.sawrose.cryptotracker.core.data.networking.constructUrl
import com.sawrose.cryptotracker.core.data.networking.safeCall
import com.sawrose.cryptotracker.core.domain.util.NetworkError
import com.sawrose.cryptotracker.core.domain.util.Result
import com.sawrose.cryptotracker.core.domain.util.map
import com.sawrose.cryptotracker.crypto.data.mappers.toCoin
import com.sawrose.cryptotracker.crypto.data.networking.dto.CoinDto
import com.sawrose.cryptotracker.crypto.data.networking.dto.CoinResponseDto
import com.sawrose.cryptotracker.crypto.domain.Coin
import com.sawrose.cryptotracker.crypto.domain.CoinDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class RemoteCoinDataSource(
    private val httpClient: HttpClient
) : CoinDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return safeCall<CoinResponseDto> {
            httpClient.get(
                urlString = constructUrl("/assets")
            )
        }.map { response ->
            response.data.map(CoinDto::toCoin)

        }
    }
}