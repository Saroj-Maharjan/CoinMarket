package com.sawrose.cryptotracker.crypto.data.store

import com.sawrose.cryptotracker.core.domain.util.Result
import com.sawrose.cryptotracker.crypto.data.local.CoinDao
import com.sawrose.cryptotracker.crypto.data.mappers.toCoinPrice
import com.sawrose.cryptotracker.crypto.data.mappers.toCoinPriceEntity
import com.sawrose.cryptotracker.crypto.domain.CoinDataSource
import com.sawrose.cryptotracker.crypto.domain.CoinPrice
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.MutableStoreBuilder
import org.mobilenativefoundation.store.store5.OnUpdaterCompletion
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Updater
import org.mobilenativefoundation.store.store5.UpdaterResult
import java.time.ZonedDateTime

data class CoinHistoryKey(
    val coinId: String,
    val start: ZonedDateTime,
    val endRange: ZonedDateTime
)

@OptIn(ExperimentalStoreApi::class)
fun createCoinHistoryStore(
    remoteCoinDataSource: CoinDataSource,
    coinDao: CoinDao
): MutableStore<CoinHistoryKey, List<CoinPrice>> {
    val coinHistoryUpdater = object : Updater<CoinHistoryKey, List<CoinPrice>, Unit> {
        override val onCompletion: OnUpdaterCompletion<Unit>? = null
        override suspend fun post(key: CoinHistoryKey, value: List<CoinPrice>): UpdaterResult =
            UpdaterResult.Success.Typed(Unit)
    }

    val coinHistoryBookkeeper = RoomBookkeeper<CoinHistoryKey>(coinDao) { key ->
        "${key.coinId}_${key.start.toInstant().toEpochMilli()}_${key.endRange.toInstant().toEpochMilli()}"
    }

    val coinHistoryConverter = object : Converter<List<CoinPrice>, List<CoinPrice>, List<CoinPrice>> {
        override fun fromNetworkToLocal(network: List<CoinPrice>): List<CoinPrice> = network
        override fun fromOutputToLocal(output: List<CoinPrice>): List<CoinPrice> = output
    }

    return MutableStoreBuilder.from<CoinHistoryKey, List<CoinPrice>, List<CoinPrice>, List<CoinPrice>>(
        fetcher = Fetcher.of { key ->
            when (val result = remoteCoinDataSource.getCoinHistory(key.coinId, key.start, key.endRange)) {
                is Result.Success -> result.data
                is Result.Error -> throw Exception(result.error.name)
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key ->
                coinDao.observeHistory(key.coinId).map { entities ->
                    if (entities.isEmpty()) null else entities.map { it.toCoinPrice() }
                }
            },
            writer = { key, prices ->
                coinDao.deleteHistory(key.coinId)
                coinDao.insertHistory(prices.map { it.toCoinPriceEntity(key.coinId) })
            },
            delete = { key ->
                coinDao.deleteHistory(key.coinId)
            },
            deleteAll = {
                // Not applicable / no-op
            }
        ),
        converter = coinHistoryConverter
    ).build(
        updater = coinHistoryUpdater,
        bookkeeper = coinHistoryBookkeeper
    )
}
