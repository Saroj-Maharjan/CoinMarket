package com.sawrose.cryptotracker.crypto.data.local

import androidx.room.Entity
import java.time.ZonedDateTime

@Entity(
    tableName = "coin_prices",
    primaryKeys = ["coinId", "dateTime"]
)
data class CoinPriceEntity(
    val coinId: String,
    val priceUsd: Double,
    val dateTime: ZonedDateTime
)
