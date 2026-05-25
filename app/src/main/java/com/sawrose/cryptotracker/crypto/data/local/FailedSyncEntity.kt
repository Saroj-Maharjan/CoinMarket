package com.sawrose.cryptotracker.crypto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "failed_syncs")
data class FailedSyncEntity(
    @PrimaryKey val key: String,
    val timestamp: Long
)
