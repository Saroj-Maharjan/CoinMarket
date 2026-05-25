package com.sawrose.cryptotracker.crypto.data.store

import com.sawrose.cryptotracker.core.domain.util.Result
import com.sawrose.cryptotracker.crypto.data.local.CoinDao
import com.sawrose.cryptotracker.crypto.data.mappers.toCoin
import com.sawrose.cryptotracker.crypto.data.mappers.toCoinEntity
import com.sawrose.cryptotracker.crypto.domain.Coin
import com.sawrose.cryptotracker.crypto.domain.CoinDataSource
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

@OptIn(ExperimentalStoreApi::class)
fun createCoinStore(
    remoteCoinDataSource: CoinDataSource,
    coinDao: CoinDao
): MutableStore<Unit, List<Coin>> {
    val coinUpdater = object : Updater<Unit, List<Coin>, Unit> {
        override val onCompletion: OnUpdaterCompletion<Unit>? = null
        override suspend fun post(key: Unit, value: List<Coin>): UpdaterResult =
            UpdaterResult.Success.Typed(Unit)
    }

    val coinBookkeeper = RoomBookkeeper<Unit>(coinDao) { "COINS_LIST" }

    val coinConverter = object : Converter<List<Coin>, List<Coin>, List<Coin>> {
        override fun fromNetworkToLocal(network: List<Coin>): List<Coin> = network
        override fun fromOutputToLocal(output: List<Coin>): List<Coin> = output
    }

    return MutableStoreBuilder.from<Unit, List<Coin>, List<Coin>, List<Coin>>(
        fetcher = Fetcher.of {
            when (val result = remoteCoinDataSource.getCoins()) {
                is Result.Success -> result.data
                is Result.Error -> throw Exception(result.error.name)
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = {
                coinDao.observeCoins().map { entities ->
                    if (entities.isEmpty()) null else entities.map { it.toCoin() }
                }
            },
            writer = { _, coins ->
                coinDao.deleteAllCoins()
                coinDao.insertCoins(coins.map { it.toCoinEntity() })
            },
            delete = {
                coinDao.deleteAllCoins()
            },
            deleteAll = {
                coinDao.deleteAllCoins()
            }
        ),
        converter = coinConverter
    ).build(
        updater = coinUpdater,
        bookkeeper = coinBookkeeper
    )
}
