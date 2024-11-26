package com.kobeza.sv.currencyexchanger.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            MainScreen(
                state = state,
                action = { action ->
                    when (action) {
                        is MainScreenAction.CurrencyToBuySelected -> viewModel.currencyToBuySelected(
                            action.currency
                        )

                        is MainScreenAction.CurrencyToSellSelected -> viewModel.currencyToSellSelected(
                            action.currency
                        )

                        is MainScreenAction.AmountToSellSelected -> viewModel.amountToSellSelected(
                            action.amount
                        )

                        MainScreenAction.Retry -> viewModel.retry()
                        MainScreenAction.Exchange -> viewModel.exchangeCurrency()
                        MainScreenAction.SuccessDialogShown -> viewModel.done()
                    }
                }
            )
        }
    }
}