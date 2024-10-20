package com.sawrose.cryptotracker.presentation.coin_list

import com.sawrose.cryptotracker.presentation.model.CoinUI

sealed interface CoinListAction {
    data class OnCoinClicked(val coinUI: CoinUI) : CoinListAction
    data object onRefresh : CoinListAction
}