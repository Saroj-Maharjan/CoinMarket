package com.sawrose.cryptotracker.di

import androidx.room.Room
import com.sawrose.cryptotracker.core.data.networking.HttpClientFactory
import com.sawrose.cryptotracker.crypto.data.OfflineFirstCoinRepository
import com.sawrose.cryptotracker.crypto.data.local.CryptoDatabase
import com.sawrose.cryptotracker.crypto.data.networking.RemoteCoinDataSource
import com.sawrose.cryptotracker.crypto.data.store.CoinHistoryKey
import com.sawrose.cryptotracker.crypto.data.store.createCoinHistoryStore
import com.sawrose.cryptotracker.crypto.data.store.createCoinStore
import com.sawrose.cryptotracker.crypto.domain.Coin
import com.sawrose.cryptotracker.crypto.domain.CoinDataSource
import com.sawrose.cryptotracker.crypto.domain.CoinPrice
import com.sawrose.cryptotracker.crypto.domain.CoinRepository
import com.sawrose.cryptotracker.crypto.presentation.coin_list.CoinListViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore

@OptIn(ExperimentalStoreApi::class)
val appModule = module {
    single { HttpClientFactory.create(CIO.create()) }
    singleOf(::RemoteCoinDataSource).bind<CoinDataSource>()

    // Room Database & DAO
    single {
        Room.databaseBuilder(
            androidContext(),
            CryptoDatabase::class.java,
            "crypto.db"
        ).build()
    }
    single { get<CryptoDatabase>().coinDao }

    // Store5
    single(org.koin.core.qualifier.named("coinStore")) { createCoinStore(get(), get()) }
    single(org.koin.core.qualifier.named("coinHistoryStore")) { createCoinHistoryStore(get(), get()) }

    // Repository
    single<CoinRepository> {
        OfflineFirstCoinRepository(
            coinStore = get(org.koin.core.qualifier.named("coinStore")),
            coinHistoryStore = get(org.koin.core.qualifier.named("coinHistoryStore"))
        )
    }

    viewModelOf(::CoinListViewModel)
}