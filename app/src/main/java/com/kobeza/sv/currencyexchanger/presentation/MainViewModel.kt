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
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ratesUseCase: GetCurrencyRatesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UIState())
    val state = _state.asStateFlow()

    private var currencyRates = emptyMap<String, Double>()
    private val fakeBalanceRemoteStorage = mutableMapOf(BASE_CURRENCY to INITIAL_AMOUNT)
    private var fakeExchangeOperationsCount = 0

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        _state.update {
            it.copy(
                isLoading = false,
                dialogInfo = DialogInfo.ErrorDialog
            )
        }
    }

    init {
        loadRates()
    }

    private fun loadRates() {
        viewModelScope.launch(errorHandler) {
            _state.update {
                it.copy(
                    isLoading = true,
                    balance = fakeBalanceRemoteStorage.map {
                        "${formatAmount(it.value)} ${it.key}"
                    },
                )
            }
            while (true) {
                parseRates(ratesUseCase())
                delay(RATES_UPDATE_DELAY)
            }
        }
    }

    private fun parseRates(rates: CurrencyExchangeRates) {
        currencyRates = rates.rates
        _state.update {
            it.copy(
                isLoading = false,
                availableCurrencies = currencyRates.keys,
            )
        }
    }

    fun currencyToSellSelected(currency: String) {
        _state.update {
            it.copy(
                currencyToSell = currency
            )
        }
        preCalculateExchange()
    }

    fun currencyToBuySelected(currency: String) {
        _state.update {
            it.copy(
                currencyToBuy = currency
            )
        }
        preCalculateExchange()
    }

    fun amountToSellSelected(amount: String) {
        _state.update {
            it.copy(
                amountToSell = amount
            )
        }
        preCalculateExchange()
    }

    private fun preCalculateExchange() {
        with(state.value) {
            val convertedAmount = convertToAmount(amountToSell) ?: return
            val currentAmount =
                fakeBalanceRemoteStorage.getOrDefault(currencyToSell, DEFAULT_DOUBLE)
            val isExchangeButtonActive =
                currentAmount + getCommission(currentAmount) >= convertedAmount
            val rate = currencyRates[currencyToBuy]
            val amount = if (currencyToSell == BASE_CURRENCY) {
                convertedAmount
            } else {
                convertToBaseCurrency(
                    amount = convertedAmount,
                    currency = currencyToSell,
                )
            }

            if (amount == null || rate == null) {
                _state.update {
                    it.copy(
                        dialogInfo = DialogInfo.ErrorDialog
                    )
                }
                return
            }

            val amountToBuy = amount * rate
            _state.update { stateModel ->
                stateModel.copy(
                    amountToBuy = if (amountToBuy > DEFAULT_DOUBLE) "+${formatAmount(amountToBuy)}" else "",
                    isExchangeButtonActive = isExchangeButtonActive
                )
            }
        }
    }

    private fun getCommission(amount: Double): Double {
        return if (fakeExchangeOperationsCount > FREE_OPERATIONS_MAX_COUNT) {
            amount * DEFAULT_COMMISSION
        } else DEFAULT_DOUBLE
    }

    private fun convertToAmount(amount: String) = kotlin.runCatching {
        BigDecimal(amount).toDouble()
    }.getOrNull()

    private fun convertToBaseCurrency(
        amount: Double?,
        currency: String,
    ): Double? {
        if (amount == null) return null
        return currencyRates[currency]?.let { rate ->
            amount / rate
        }
    }

    fun retry() {
        loadRates()
        clearFields()
    }

    fun done() {
        clearFields()
    }

    private fun clearFields() {
        _state.update {
            it.copy(
                dialogInfo = null,
                amountToSell = "",
                amountToBuy = "",
                currencyToSell = BASE_CURRENCY,
            )
        }
    }

    fun exchangeCurrency() {
        with(state.value) {
            val originalAmount = convertToAmount(amountToSell)
            val rate = currencyRates[currencyToBuy]

            val convertedAmount = if (currencyToSell == BASE_CURRENCY) {
                originalAmount
            } else {
                convertToBaseCurrency(
                    amount = originalAmount,
                    currency = currencyToSell,
                )
            }

            if (originalAmount == null || rate == null || convertedAmount == null) {
                _state.update {
                    it.copy(
                        dialogInfo = DialogInfo.ErrorDialog
                    )
                }
                return
            }

            val amountReceived = convertedAmount * rate
            val commission = getCommission(originalAmount)
            fakeBalanceRemoteStorage[currencyToSell] =
                fakeBalanceRemoteStorage.getOrDefault(
                    currencyToSell,
                    DEFAULT_DOUBLE
                ) - (originalAmount + commission)
            fakeBalanceRemoteStorage[currencyToBuy] =
                fakeBalanceRemoteStorage.getOrDefault(
                    currencyToBuy,
                    DEFAULT_DOUBLE
                ) + amountReceived
            _state.update {
                it.copy(
                    balance = fakeBalanceRemoteStorage.map {
                        "${formatAmount(it.value)} ${it.key}"
                    },
                    dialogInfo = DialogInfo.SuccessDialogInfo(
                        amountSold = "${formatAmount(originalAmount)} $currencyToSell",
                        amountReceived = "${formatAmount(amountReceived)} $currencyToBuy",
                        commission = if (commission > DEFAULT_DOUBLE) "${formatAmount(commission)} $currencyToSell" else "",
                    )
                )
            }
            fakeExchangeOperationsCount += 1
        }
    }

    private fun formatAmount(amount: Double) = String.format(
        Locale.getDefault(),
        "%.2f",
        amount
    )

    data class UIState(
        val isLoading: Boolean = false,
        val availableCurrencies: Set<String> = emptySet(),
        val balance: List<String> = emptyList(),
        val currencyToSell: String = BASE_CURRENCY,
        val currencyToBuy: String = BASE_CURRENCY,
        val amountToSell: String = "",
        val amountToBuy: String = "",
        val dialogInfo: DialogInfo? = null,
        val isExchangeButtonActive: Boolean = false,
    )

    sealed interface DialogInfo {
        data class SuccessDialogInfo(
            val amountSold: String,
            val amountReceived: String,
            val commission: String,
        ) : DialogInfo

        data object ErrorDialog : DialogInfo
    }

    companion object {
        private const val BASE_CURRENCY = "EUR"
        private const val INITIAL_AMOUNT = 1000.0
        private const val RATES_UPDATE_DELAY = 5_000L
        private const val FREE_OPERATIONS_MAX_COUNT = 4
        private const val DEFAULT_DOUBLE = 0.0
        private const val DEFAULT_COMMISSION = 0.007
    }
}