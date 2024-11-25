package com.kobeza.sv.currencyexchanger.data.datasource.remote.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CurrencyExchangeRatesResponse(
    @SerializedName("base") val base: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("rates") val rates: Map<String, Double>?
)