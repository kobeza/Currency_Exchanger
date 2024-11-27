package com.kobeza.sv.currencyexchanger.data.datasource.mapper

import com.kobeza.sv.currencyexchanger.data.datasource.remote.response.CurrencyExchangeRatesResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyExchangeRatesMapperTest {

    private val mapper = CurrencyExchangeRatesMapper()

    @Test
    fun `mapToModel should map valid response correctly`() {
        val response = CurrencyExchangeRatesResponse(
            base = "EUR",
            date = "2022-10-06",
            rates = mapOf("USD" to 1.12, "GBP" to 0.85)
        )
        val result = mapper.mapToModel(response)
        assertEquals("EUR", result.base)
        assertEquals("2022-10-06", result.date)
        assertEquals(mapOf("USD" to 1.12, "GBP" to 0.85), result.rates)
    }

    @Test
    fun `mapToModel should return default values for null fields`() {
        val response = CurrencyExchangeRatesResponse(
            base = null,
            date = null,
            rates = null
        )
        val result = mapper.mapToModel(response)
        assertEquals("", result.base)
        assertEquals("", result.date)
        assertEquals(emptyMap<String, Double>(), result.rates)
    }

    @Test
    fun `mapToModel should handle empty response`() {
        val response = CurrencyExchangeRatesResponse(
            base = "",
            date = "",
            rates = emptyMap()
        )
        val result = mapper.mapToModel(response)
        assertEquals("", result.base)
        assertEquals("", result.date)
        assertEquals(emptyMap<String, Double>(), result.rates)
    }
}