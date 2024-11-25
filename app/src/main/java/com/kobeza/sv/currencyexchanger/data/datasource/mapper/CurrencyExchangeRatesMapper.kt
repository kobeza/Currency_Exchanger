package com.kobeza.sv.currencyexchanger.data.datasource.mapper

import com.kobeza.sv.currencyexchanger.data.datasource.remote.response.CurrencyExchangeRatesResponse
import com.kobeza.sv.currencyexchanger.domain.model.CurrencyExchangeRates
import javax.inject.Inject

class CurrencyExchangeRatesMapper @Inject constructor() {
    fun mapToModel(response: CurrencyExchangeRatesResponse): CurrencyExchangeRates {
        return CurrencyExchangeRates(
            base = response.base.orEmpty(),
            date = response.date.orEmpty(),
            rates = response.rates.orEmpty(),
        )
    }
}