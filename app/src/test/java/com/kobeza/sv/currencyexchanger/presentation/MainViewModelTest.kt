package com.kobeza.sv.currencyexchanger.presentation


import app.cash.turbine.test
import com.kobeza.sv.currencyexchanger.domain.model.CurrencyExchangeRates
import com.kobeza.sv.currencyexchanger.domain.usecase.GetCurrencyRatesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val ratesUseCase: GetCurrencyRatesUseCase = mockk()
    private lateinit var viewModel: MainViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(ratesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun currencyToSellSelected() = runTest {
        val mockRates = CurrencyExchangeRates(
            base = "EUR",
            date = "2022-10-06",
            rates = mapOf("USD" to 1.1, "GBP" to 0.9)
        )
        coEvery { ratesUseCase() } returns mockRates

        viewModel.currencyToSellSelected("USD")
        coVerify { ratesUseCase() }
        viewModel.state.test {
            assertFalse { awaitItem().isLoading }
            assertTrue { awaitItem().isLoading }
            val thirdEmission = awaitItem()
            assertFalse { thirdEmission.isLoading }
            assertFalse(thirdEmission.isExchangeButtonActive)
            assertEquals(null, thirdEmission.dialogInfo)
            assertEquals("", thirdEmission.amountToSell)
            assertEquals("", thirdEmission.amountToBuy)
            assertEquals("USD", thirdEmission.currencyToSell)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun currencyToBuySelected() = runTest {
        val mockRates = CurrencyExchangeRates(
            base = "EUR",
            date = "2022-10-06",
            rates = mapOf("USD" to 1.1, "GBP" to 0.9)
        )
        coEvery { ratesUseCase() } returns mockRates

        viewModel.currencyToBuySelected("USD")
        coVerify { ratesUseCase() }
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(false, viewModel.state.value.isExchangeButtonActive)
        assertEquals("", viewModel.state.value.amountToSell)
        assertEquals("", viewModel.state.value.amountToBuy)
        assertEquals("EUR", viewModel.state.value.currencyToSell)
        assertEquals("USD", viewModel.state.value.currencyToBuy)
    }

    @Test
    fun amountToSellSelected() = runTest {
        val mockRates = CurrencyExchangeRates(
            base = "EUR",
            date = "2022-10-06",
            rates = mapOf("USD" to 1.1, "GBP" to 0.9)
        )
        coEvery { ratesUseCase() } returns mockRates

        viewModel.amountToSellSelected("100.0")
        coVerify { ratesUseCase() }
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(false, viewModel.state.value.isExchangeButtonActive)
        assertEquals("100.0", viewModel.state.value.amountToSell)
        assertEquals("", viewModel.state.value.amountToBuy)
        assertEquals("EUR", viewModel.state.value.currencyToSell)
        assertEquals("EUR", viewModel.state.value.currencyToBuy)
    }

    @Test
    fun retry() = runTest {
        val mockRates = CurrencyExchangeRates(
            base = "EUR",
            date = "2022-10-06",
            rates = mapOf("USD" to 1.1, "GBP" to 0.9)
        )
        coEvery { ratesUseCase() } returns mockRates

        coVerify { ratesUseCase() }
        viewModel.state.test {
            assertFalse { awaitItem().isLoading }
            viewModel.retry()
            val thirdEmission = awaitItem()
            assertFalse { thirdEmission.isLoading }
            assertFalse(thirdEmission.isExchangeButtonActive)
            assertEquals("", thirdEmission.amountToSell)
            assertEquals("", thirdEmission.amountToBuy)
            assertEquals("EUR", thirdEmission.currencyToSell)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun done() {
        viewModel.done()
        assertEquals(null, viewModel.state.value.dialogInfo)
        assertEquals("", viewModel.state.value.amountToSell)
        assertEquals("", viewModel.state.value.amountToBuy)
        assertEquals("EUR", viewModel.state.value.currencyToSell)
    }

    @Test
    fun exchangeCurrency() = runTest {
        val mockRates = CurrencyExchangeRates(
            base = "EUR",
            date = "2022-10-06",
            rates = mapOf("USD" to 2.0, "GBP" to 0.9)
        )
        val dialogInfo = MainViewModel.DialogInfo.SuccessDialogInfo(
            amountSold = "100.00 EUR",
            amountReceived = "200.00 USD",
            commission = "",
        )
        coEvery { ratesUseCase() } returns mockRates

        viewModel.retry()
        coVerify { ratesUseCase() }

        viewModel.currencyToSellSelected("EUR")
        viewModel.currencyToBuySelected("USD")
        viewModel.amountToSellSelected("100.0")
        viewModel.exchangeCurrency()
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(false, viewModel.state.value.isExchangeButtonActive)
        assertEquals(dialogInfo, viewModel.state.value.dialogInfo)
        assertEquals("", viewModel.state.value.amountToSell)
        assertEquals("", viewModel.state.value.amountToBuy)
        assertEquals("EUR", viewModel.state.value.currencyToSell)
    }
}