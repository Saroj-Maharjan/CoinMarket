package com.sawrose.cryptotracker.crypto.data.store

import com.sawrose.cryptotracker.crypto.data.local.CoinDao
import com.sawrose.cryptotracker.crypto.data.local.FailedSyncEntity
import org.mobilenativefoundation.store.store5.Bookkeeper

class RoomBookkeeper<Key : Any>(
    private val coinDao: CoinDao,
    private val keySerializer: (Key) -> String
) : Bookkeeper<Key> {
    override suspend fun getLastFailedSync(key: Key): Long? {
        return coinDao.getFailedSync(keySerializer(key))
    }

    override suspend fun setLastFailedSync(key: Key, timestamp: Long): Boolean {
        coinDao.insertFailedSync(FailedSyncEntity(keySerializer(key), timestamp))
        return true
    }

    override suspend fun clear(key: Key): Boolean {
        coinDao.deleteFailedSync(keySerializer(key))
        return true
    }

    override suspend fun clearAll(): Boolean {
        coinDao.deleteAllFailedSyncs()
        return true
    }
}
