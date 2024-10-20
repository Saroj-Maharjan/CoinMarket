package com.sawrose.cryptotracker.crypto.data.mappers

import com.sawrose.cryptotracker.crypto.data.networking.dto.CoinDto
import com.sawrose.cryptotracker.crypto.domain.Coin

fun CoinDto.toCoin(): Coin = Coin(
    id = id,
    rank = rank,
    name = name,
    symbol = symbol,
    marketCapUsd = marketCapUsd,
    priceUsd = priceUsd,
    changePercent24Hr = changePercent24Hr
)