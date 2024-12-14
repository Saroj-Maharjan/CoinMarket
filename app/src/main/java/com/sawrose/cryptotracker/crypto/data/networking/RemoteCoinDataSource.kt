package com.sawrose.cryptotracker.crypto.data.networking

import com.sawrose.cryptotracker.core.data.networking.constructUrl
import com.sawrose.cryptotracker.core.data.networking.safeCall
import com.sawrose.cryptotracker.core.domain.util.NetworkError
import com.sawrose.cryptotracker.core.domain.util.Result
import com.sawrose.cryptotracker.core.domain.util.map
import com.sawrose.cryptotracker.crypto.data.mappers.toCoin
import com.sawrose.cryptotracker.crypto.data.mappers.toCoinPrice
import com.sawrose.cryptotracker.crypto.data.networking.dto.CoinDto
import com.sawrose.cryptotracker.crypto.data.networking.dto.CoinHistoryDto
import com.sawrose.cryptotracker.crypto.data.networking.dto.CoinPriceDto
import com.sawrose.cryptotracker.crypto.data.networking.dto.CoinResponseDto
import com.sawrose.cryptotracker.crypto.domain.Coin
import com.sawrose.cryptotracker.crypto.domain.CoinDataSource
import com.sawrose.cryptotracker.crypto.domain.CoinPrice
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.ZoneId
import java.time.ZonedDateTime

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

    override suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        endRange: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> {
        val startMillis = start
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        val endMillis = endRange
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        return safeCall<CoinHistoryDto> {
            httpClient.get(
                urlString = constructUrl("/assets/$coinId/history")
            ){
                parameter("interval", "h6")
                parameter("start", startMillis)
                parameter("end", endMillis)
            }
        }.map {response ->
            response.data.map(CoinPriceDto::toCoinPrice)
        }
    }
}