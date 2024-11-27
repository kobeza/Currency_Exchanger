package com.kobeza.sv.currencyexchanger.domain.usecase

import com.kobeza.sv.currencyexchanger.domain.model.CurrencyExchangeRates
import com.kobeza.sv.currencyexchanger.domain.repository.CurrencyExchangeRatesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCurrencyRatesUseCaseTest {

    private val repository: CurrencyExchangeRatesRepository = mockk()
    private val useCase = GetCurrencyRatesUseCase(repository)

    @Test
    fun `invoke should return currency rates from repository`() = runBlocking {
        val expectedRates = CurrencyExchangeRates(
            base = "EUR",
            date = "2022-10-06",
            rates = mapOf("USD" to 1.12, "GBP" to 0.85)
        )
        coEvery { repository.getRates() } returns expectedRates
        val result = useCase.invoke()
        coVerify { repository.getRates() }
        assertEquals(expectedRates, result)
    }

    @Test(expected = Exception::class)
    fun `invoke should throw exception if repository throws`(): Unit = runBlocking {
        coEvery { repository.getRates() } throws Exception("Repository error")
        useCase.invoke()
    }
}