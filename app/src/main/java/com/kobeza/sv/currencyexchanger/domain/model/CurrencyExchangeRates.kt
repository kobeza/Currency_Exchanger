package com.kobeza.sv.currencyexchanger.domain.model


data class CurrencyExchangeRates(
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
)