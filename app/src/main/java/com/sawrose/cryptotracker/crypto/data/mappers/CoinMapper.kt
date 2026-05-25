package com.sawrose.cryptotracker.crypto.data.mappers

import com.sawrose.cryptotracker.crypto.data.local.CoinEntity
import com.sawrose.cryptotracker.crypto.data.local.CoinPriceEntity
import com.sawrose.cryptotracker.crypto.data.networking.dto.CoinDto
import com.sawrose.cryptotracker.crypto.data.networking.dto.CoinPriceDto
import com.sawrose.cryptotracker.crypto.domain.Coin
import com.sawrose.cryptotracker.crypto.domain.CoinPrice
import java.time.Instant
import java.time.ZoneId

fun CoinDto.toCoin(): Coin = Coin(
    id = id,
    rank = rank,
    name = name,
    symbol = symbol,
    marketCapUsd = marketCapUsd,
    priceUsd = priceUsd,
    changePercent24Hr = changePercent24Hr
)

fun CoinPriceDto.toCoinPrice(): CoinPrice = CoinPrice(
    priceUsd = priceUsd,
    dateTime = Instant
        .ofEpochMilli(time)
        .atZone(ZoneId.systemDefault())
)

fun CoinEntity.toCoin(): Coin = Coin(
    id = id,
    rank = rank,
    name = name,
    symbol = symbol,
    marketCapUsd = marketCapUsd,
    priceUsd = priceUsd,
    changePercent24Hr = changePercent24Hr
)

fun Coin.toCoinEntity(): CoinEntity = CoinEntity(
    id = id,
    rank = rank,
    name = name,
    symbol = symbol,
    marketCapUsd = marketCapUsd,
    priceUsd = priceUsd,
    changePercent24Hr = changePercent24Hr
)

fun CoinPriceEntity.toCoinPrice(): CoinPrice = CoinPrice(
    priceUsd = priceUsd,
    dateTime = dateTime
)

fun CoinPrice.toCoinPriceEntity(coinId: String): CoinPriceEntity = CoinPriceEntity(
    coinId = coinId,
    priceUsd = priceUsd,
    dateTime = dateTime
)