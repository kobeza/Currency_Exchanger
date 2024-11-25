package com.kobeza.sv.currencyexchanger.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobeza.sv.currencyexchanger.domain.model.CurrencyExchangeRates
import com.kobeza.sv.currencyexchanger.domain.usecase.GetCurrencyRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ratesUseCase: GetCurrencyRatesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UIState())
    val state = _state.asStateFlow()

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught exception: ${exception.message}")
    }

    init {
        loadRates()
    }

    private fun loadRates() {
        viewModelScope.launch(errorHandler) {
            updateState(
                isLoading = true
            )
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            while (true) {
                parseRates(ratesUseCase())
                delay(RATES_UPDATE_DELAY)
            }
        }
    }

    private fun parseRates(rates: CurrencyExchangeRates){
        print(rates)
    }

    private fun updateState(
        isLoading: Boolean = false,
    ){
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    data class UIState(
        val isLoading: Boolean = false,
    )

    companion object{
        private const val RATES_UPDATE_DELAY = 5_000L
    }
}