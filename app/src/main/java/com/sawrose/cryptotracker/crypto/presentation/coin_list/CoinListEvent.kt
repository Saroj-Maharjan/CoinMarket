package com.sawrose.cryptotracker.crypto.presentation.coin_list

import com.sawrose.cryptotracker.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(val error: NetworkError) : CoinListEvent
}