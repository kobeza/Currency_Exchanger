package com.kobeza.sv.currencyexchanger.domain.repository

import com.kobeza.sv.currencyexchanger.domain.model.CurrencyExchangeRates

interface CurrencyExchangeRatesRepository {
    suspend fun getRates(): CurrencyExchangeRates
}