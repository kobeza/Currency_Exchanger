package com.kobeza.sv.currencyexchanger.data.datasource.remote.api

import com.kobeza.sv.currencyexchanger.data.datasource.remote.response.CurrencyExchangeRatesResponse
import retrofit2.http.GET

interface CurrencyExchangeRatesApi {
    @GET("tasks/api/currency-exchange-rates")
    suspend fun getRates(): CurrencyExchangeRatesResponse
}