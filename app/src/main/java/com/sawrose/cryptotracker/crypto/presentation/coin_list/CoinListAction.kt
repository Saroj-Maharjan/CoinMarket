package com.sawrose.cryptotracker.crypto.presentation.coin_list

import com.sawrose.cryptotracker.crypto.presentation.model.CoinUI

sealed interface CoinListAction {
    data class OnCoinClicked(val coinUI: CoinUI) : CoinListAction
}