package com.sawrose.cryptotracker.presentation.coin_list

import androidx.compose.runtime.Immutable
import com.sawrose.cryptotracker.presentation.model.CoinUI

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<CoinUI> = emptyList(),
    val selectedCoin: CoinUI? = null
)
