package com.kobeza.sv.currencyexchanger.domain.usecase

import com.kobeza.sv.currencyexchanger.domain.model.CurrencyExchangeRates
import com.kobeza.sv.currencyexchanger.domain.repository.CurrencyExchangeRatesRepository
import javax.inject.Inject

class GetCurrencyRatesUseCase @Inject constructor(
    private val repository: CurrencyExchangeRatesRepository,
) {
    suspend operator fun invoke(): CurrencyExchangeRates {
        return repository.getRates()
    }
}