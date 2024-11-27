package com.kobeza.sv.currencyexchanger.data.datasource.repository

import com.kobeza.sv.currencyexchanger.data.datasource.mapper.CurrencyExchangeRatesMapper
import com.kobeza.sv.currencyexchanger.data.datasource.remote.api.CurrencyExchangeRatesApi
import com.kobeza.sv.currencyexchanger.data.datasource.remote.response.CurrencyExchangeRatesResponse
import com.kobeza.sv.currencyexchanger.domain.model.CurrencyExchangeRates
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CurrencyExchangeRatesRepositoryImplTest {

    private lateinit var api: CurrencyExchangeRatesApi
    private lateinit var mapper: CurrencyExchangeRatesMapper
    private lateinit var repository: CurrencyExchangeRatesRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        mapper = mockk()
        repository = CurrencyExchangeRatesRepositoryImpl(api, mapper)
    }

    @Test
    fun `getRates should call API and map the result`() = runBlocking {
        // Arrange
        val response = CurrencyExchangeRatesResponse(
            base = "EUR",
            date = "2022-10-06",
            rates = mapOf("USD" to 1.12, "GBP" to 0.85)
        )
        val mappedModel = CurrencyExchangeRates(
            base = "EUR",
            date = "2022-10-06",
            rates = mapOf("USD" to 1.12, "GBP" to 0.85)
        )

        coEvery { api.getRates() } returns response
        every { mapper.mapToModel(response) } returns mappedModel
        val result = repository.getRates()
        coVerify { api.getRates() }
        verify { mapper.mapToModel(response) }
        assertEquals(mappedModel, result)
    }

    @Test(expected = Exception::class)
    fun `getRates should throw exception if API fails`(): Unit = runBlocking {
        coEvery { api.getRates() } throws Exception("API error")
        repository.getRates()
    }
}