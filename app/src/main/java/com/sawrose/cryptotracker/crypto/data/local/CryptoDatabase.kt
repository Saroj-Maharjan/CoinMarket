package com.sawrose.cryptotracker.crypto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CoinEntity::class, CoinPriceEntity::class, FailedSyncEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CryptoDatabase : RoomDatabase() {
    abstract val coinDao: CoinDao
}
