package com.kobeza.sv.currencyexchanger.data.datasource.repository

import com.kobeza.sv.currencyexchanger.data.datasource.mapper.CurrencyExchangeRatesMapper
import com.kobeza.sv.currencyexchanger.data.datasource.remote.api.CurrencyExchangeRatesApi
import com.kobeza.sv.currencyexchanger.domain.model.CurrencyExchangeRates
import com.kobeza.sv.currencyexchanger.domain.repository.CurrencyExchangeRatesRepository
import javax.inject.Inject

class CurrencyExchangeRatesRepositoryImpl @Inject constructor(
    private val api: CurrencyExchangeRatesApi,
    private val mapper: CurrencyExchangeRatesMapper,
): CurrencyExchangeRatesRepository {
    override suspend fun getRates(): CurrencyExchangeRates {
        return mapper.mapToModel(api.getRates())
    }
}