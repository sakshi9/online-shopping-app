package com.example.onlineshopping.viewModel

import app.cash.turbine.test
import com.example.onlineshopping.data.model.Address
import com.example.onlineshopping.data.model.Order
import com.example.onlineshopping.domain.usecase.CheckoutSummary
import com.example.onlineshopping.domain.usecase.GetCheckoutSummaryUseCase
import com.example.onlineshopping.domain.usecase.PlaceOrderUseCase
import com.example.onlineshopping.domain.usecase.ValidateAddressUseCase
import com.example.onlineshopping.ui.model.CheckoutUiState
import com.example.onlineshopping.ui.viewModel.CheckoutViewModel
import com.example.onlineshopping.ui.viewModel.DELIVERY_SLOTS
import com.example.onlineshopping.ui.viewModel.OrderStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val validateAddressUseCase: ValidateAddressUseCase = mockk()
    private val placeOrderUseCase: PlaceOrderUseCase = mockk()
    private val getCheckoutSummaryUseCase: GetCheckoutSummaryUseCase = mockk()
    private val orderStore: OrderStore = mockk(relaxed = true)

    private lateinit var viewModel: CheckoutViewModel

    private val sampleSummary = CheckoutSummary(
        items = emptyList(),
        subtotal = 100.0,
        deliveryFee = 20.0,
        total = 120.0
    )

    private val sampleAddress = Address("Line 1", "City", "12345")
    private val sampleSlot = DELIVERY_SLOTS.first()
    private val sampleOrder = Order(
        id = "order_123",
        items = emptyList(),
        subtotal = 100.0,
        deliveryFee = 20.0,
        total = 120.0,
        address = sampleAddress,
        paymentMethod = "card",
        status = "Placed",
        estimatedDelivery = "Today",
        placedAt = "Now"
    )

    @Before
    fun setup() {
        every { getCheckoutSummaryUseCase.invoke() } returns flowOf(sampleSummary)
        viewModel = CheckoutViewModel(
            validateAddressUseCase,
            placeOrderUseCase,
            getCheckoutSummaryUseCase,
            orderStore
        )
    }

    @Test
    fun `initial state reflects checkout summary`() = runTest {
        viewModel.state.test {
            // Initial state from stateIn
            assertEquals(CheckoutUiState(), awaitItem())

            val state = awaitItem()
            assertEquals(sampleSummary.subtotal, state.subtotal, 0.01)
            assertEquals(sampleSummary.deliveryFee, state.deliveryFee, 0.01)
            assertEquals(sampleSummary.total, state.total, 0.01)
        }
    }

    @Test
    fun `onLine1Change updates state`() = runTest {
        viewModel.onLine1Change("New Address")
        viewModel.state.test {
            skipItems(1) // skip initial combined state
            assertEquals("New Address", awaitItem().line1)
        }
    }

    @Test
    fun `placeOrder failure updates error state`() = runTest {
        every {
            validateAddressUseCase(
                any(),
                any(),
                any()
            )
        } returns Result.failure(Exception("Invalid address"))

        viewModel.placeOrder()

        viewModel.state.test {
            assertEquals(CheckoutUiState(), awaitItem())

            val state = awaitItem()
            assertEquals("Invalid address", state.error)
        }
    }

    @Test
    fun `placeOrder success updates order and orderStore`() = runTest {
        val line1 = "Whitefield"
        val city = "Bengaluru"
        val pincode = "560045"

        every { validateAddressUseCase(line1, city, pincode) } returns Result.success(Unit)
        coEvery {
            placeOrderUseCase(
                address = Address(line1, city, pincode),
                paymentMethod = "card",
                selectedSlot = any()
            )
        } returns Result.success(sampleOrder)
        viewModel.placeOrder()

        advanceUntilIdle()

        viewModel.state.test {
            assertEquals(CheckoutUiState(), awaitItem())

            val state = awaitItem()
            assertEquals(sampleOrder, state.order)
            assertFalse(state.isPlacingOrder)
        }

        coVerify { orderStore.set(sampleOrder) }
    }

    @Test
    fun `placeOrder handles backend failure`() = runTest {
        every { validateAddressUseCase(any(), any(), any()) } returns Result.success(Unit)
        coEvery {
            placeOrderUseCase(
                any(),
                any(),
                any()
            )
        } returns Result.failure(Exception("Network error"))

        viewModel.placeOrder()
        advanceUntilIdle()

        viewModel.state.test {
            assertEquals(CheckoutUiState(), awaitItem())
            val state = awaitItem()
            assertEquals("Network error", state.error)
            assertFalse(state.isPlacingOrder)
        }
    }
}