package com.sawrose.cryptotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sawrose.cryptotracker.presentation.coin_list.CoinListScreen
import com.sawrose.cryptotracker.presentation.coin_list.CoinListViewModel
import com.sawrose.cryptotracker.ui.theme.CryptoTrackerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewmodel = koinViewModel<CoinListViewModel>()
                    val state = viewmodel.state.collectAsStateWithLifecycle()
                    CoinListScreen(
                        state = state.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}