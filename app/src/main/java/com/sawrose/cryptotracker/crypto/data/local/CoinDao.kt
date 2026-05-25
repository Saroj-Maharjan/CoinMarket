package com.sawrose.cryptotracker.crypto.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Query("SELECT * FROM coins ORDER BY rank ASC")
    fun observeCoins(): Flow<List<CoinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(coins: List<CoinEntity>)

    @Query("DELETE FROM coins")
    suspend fun deleteAllCoins()

    @Query("SELECT * FROM coin_prices WHERE coinId = :coinId ORDER BY dateTime ASC")
    fun observeHistory(coinId: String): Flow<List<CoinPriceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(prices: List<CoinPriceEntity>)

    @Query("DELETE FROM coin_prices WHERE coinId = :coinId")
    suspend fun deleteHistory(coinId: String)

    @Query("SELECT timestamp FROM failed_syncs WHERE `key` = :key")
    suspend fun getFailedSync(key: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFailedSync(failedSync: FailedSyncEntity)

    @Query("DELETE FROM failed_syncs WHERE `key` = :key")
    suspend fun deleteFailedSync(key: String)

    @Query("DELETE FROM failed_syncs")
    suspend fun deleteAllFailedSyncs()
}
