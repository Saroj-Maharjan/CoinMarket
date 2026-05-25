package com.sawrose.cryptotracker.crypto.data

import com.sawrose.cryptotracker.crypto.data.store.CoinHistoryKey
import com.sawrose.cryptotracker.crypto.domain.Coin
import com.sawrose.cryptotracker.crypto.domain.CoinPrice
import com.sawrose.cryptotracker.crypto.domain.CoinRepository
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest
import java.time.ZonedDateTime

@OptIn(ExperimentalStoreApi::class)
class OfflineFirstCoinRepository(
    private val coinStore: MutableStore<Unit, List<Coin>>,
    private val coinHistoryStore: MutableStore<CoinHistoryKey, List<CoinPrice>>
) : CoinRepository {

    override fun getCoins(): Flow<StoreReadResponse<List<Coin>>> {
        return coinStore.stream<Unit>(
            StoreReadRequest.cached(
                key = Unit,
                refresh = true
            )
        )
    }

    override fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        endRange: ZonedDateTime
    ): Flow<StoreReadResponse<List<CoinPrice>>> {
        return coinHistoryStore.stream<Unit>(
            StoreReadRequest.cached(
                key = CoinHistoryKey(coinId, start, endRange),
                refresh = true
            )
        )
    }

    override suspend fun writeCoins(coins: List<Coin>) {
        coinStore.write(StoreWriteRequest.of(Unit, coins))
    }

    override suspend fun writeCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        endRange: ZonedDateTime,
        history: List<CoinPrice>
    ) {
        coinHistoryStore.write(
            StoreWriteRequest.of(
                key = CoinHistoryKey(coinId, start, endRange),
                value = history
            )
        )
    }
}
