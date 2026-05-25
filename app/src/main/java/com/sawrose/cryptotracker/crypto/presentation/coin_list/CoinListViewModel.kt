package com.sawrose.cryptotracker.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sawrose.cryptotracker.core.domain.util.NetworkError
import com.sawrose.cryptotracker.crypto.domain.Coin
import com.sawrose.cryptotracker.crypto.domain.CoinRepository
import com.sawrose.cryptotracker.crypto.presentation.coin_detail.DataPoint
import com.sawrose.cryptotracker.crypto.presentation.model.CoinUI
import com.sawrose.cryptotracker.crypto.presentation.model.toCoinUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mobilenativefoundation.store.store5.StoreReadResponse
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CoinListViewModel(
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state: StateFlow<CoinListState> = _state
        .onStart { loadCoins() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CoinListState()
        )

    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CoinListAction) {
        when (action) {
            is CoinListAction.OnCoinClicked -> {
                selectCoin(action.coinUI)
            }
        }
    }

    private fun selectCoin(coinUI: CoinUI) {
        _state.update {
            it.copy(selectedCoin = coinUI)
        }

        viewModelScope.launch {
            coinRepository.getCoinHistory(
                coinId = coinUI.id,
                start = ZonedDateTime.now().minusDays(5),
                endRange = ZonedDateTime.now()
            ).collect { response ->
                when (response) {
                    is StoreReadResponse.Loading -> {
                        // Optionally handle custom history loading if needed
                    }
                    is StoreReadResponse.Data -> {
                        val dataPoints = response.value
                            .sortedBy { it.dateTime }
                            .map {
                                DataPoint(
                                    x = it.dateTime.hour.toFloat(),
                                    y = it.priceUsd.toFloat(),
                                    xLabel = DateTimeFormatter
                                        .ofPattern("ha\nM/d")
                                        .format(it.dateTime)
                                )
                            }
                        _state.update {
                            if (it.selectedCoin?.id == coinUI.id) {
                                it.copy(selectedCoin = it.selectedCoin?.copy(coinPriceHistory = dataPoints))
                            } else {
                                it
                            }
                        }
                    }
                    is StoreReadResponse.Error -> {
                        val errorMsg = when (response) {
                            is StoreReadResponse.Error.Exception -> response.error.message
                            is StoreReadResponse.Error.Message -> response.message
                            else -> "UNKNOWN_ERROR"
                        }
                        val networkError = try {
                            NetworkError.valueOf(errorMsg ?: "")
                        } catch (e: Exception) {
                            NetworkError.UNKNOWN_ERROR
                        }
                        _events.send(CoinListEvent.Error(networkError))
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            coinRepository.getCoins().collect { response ->
                when (response) {
                    is StoreReadResponse.Loading -> {
                        _state.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is StoreReadResponse.Data -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                coins = response.value.map(Coin::toCoinUI)
                            )
                        }
                    }
                    is StoreReadResponse.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        val errorMsg = when (response) {
                            is StoreReadResponse.Error.Exception -> response.error.message
                            is StoreReadResponse.Error.Message -> response.message
                            else -> "UNKNOWN_ERROR"
                        }
                        val networkError = try {
                            NetworkError.valueOf(errorMsg ?: "")
                        } catch (e: Exception) {
                            NetworkError.UNKNOWN_ERROR
                        }
                        _events.send(CoinListEvent.Error(networkError))
                    }
                    else -> Unit
                }
            }
        }
    }

}