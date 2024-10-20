package com.sawrose.cryptotracker.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sawrose.cryptotracker.core.domain.util.onError
import com.sawrose.cryptotracker.core.domain.util.onSuccess
import com.sawrose.cryptotracker.crypto.domain.Coin
import com.sawrose.cryptotracker.crypto.domain.CoinDataSource
import com.sawrose.cryptotracker.presentation.model.toCoinUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state: StateFlow<CoinListState> = _state
        .onStart { loadCoins() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CoinListState()
        )


    fun onAction(action: CoinListAction) {
        when (action) {
            is CoinListAction.OnCoinClicked -> {

            }

            is CoinListAction.onRefresh -> {
                loadCoins()
            }
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            coinDataSource.getCoins()
                .onSuccess { coins ->
                    _state.update {
                        it.copy(isLoading = false, coins = coins.map(Coin::toCoinUI))
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

}