package com.example.onlineshopping.viewModel

import app.cash.turbine.test
import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.domain.model.CartSummary
import com.example.onlineshopping.domain.usecase.ClearCartUseCase
import com.example.onlineshopping.domain.usecase.DecrementCartItemUseCase
import com.example.onlineshopping.domain.usecase.GetCartUseCase
import com.example.onlineshopping.domain.usecase.IncrementCartItemUseCase
import com.example.onlineshopping.domain.usecase.RemoveCartItemUseCase
import com.example.onlineshopping.ui.model.CartUiState
import com.example.onlineshopping.ui.viewModel.CartViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private var getCartUseCase: GetCartUseCase = mockk<GetCartUseCase>()
    private var incrementCartItemUseCase: IncrementCartItemUseCase =
        mockk<IncrementCartItemUseCase>()
    private var decrementCartItemUseCase: DecrementCartItemUseCase =
        mockk<DecrementCartItemUseCase>()
    private var removeCartItemUseCase: RemoveCartItemUseCase = mockk<RemoveCartItemUseCase>()
    private var clearCartUseCase: ClearCartUseCase = mockk<ClearCartUseCase>()

    private lateinit var viewModel: CartViewModel

    private val sampleCartItem = CartData(
        productId = "1",
        name = "Product 1",
        price = 100.0,
        image = "url",
        unit = "kg",
        quantity = 1
    )

    private val sampleCartSummary = CartSummary(
        items = listOf(sampleCartItem),
        subtotal = 100.0,
        deliveryFee = 40.0,
        total = 140.0
    )

    @Before
    fun setup() {
        every { getCartUseCase.invoke() } returns flowOf(sampleCartSummary)

        viewModel = CartViewModel(
            getCartUseCase,
            incrementCartItemUseCase,
            decrementCartItemUseCase,
            clearCartUseCase,
            removeCartItemUseCase
        )
    }

    @Test
    fun `uiState initially emits correct state from use case`() = runTest {
        viewModel.uiState.test {
            assertEquals(CartUiState(), awaitItem())

            val state = awaitItem()
            assertEquals(sampleCartSummary.items, state.items)
            assertEquals(sampleCartSummary.subtotal, state.subtotal, 0.01)
            assertEquals(sampleCartSummary.deliveryFee, state.deliveryFee, 0.01)
            assertEquals(sampleCartSummary.total, state.total, 0.01)
        }
    }

    @Test
    fun `increment calls clearCartUseCase`() = runTest {
        coEvery { incrementCartItemUseCase.invoke(sampleCartItem) } returns Unit
        viewModel.increment(sampleCartItem)
        advanceUntilIdle()
        coVerify { incrementCartItemUseCase.invoke(sampleCartItem) }
    }

    @Test
    fun `decrement calls clearCartUseCase`() = runTest {
        coEvery { decrementCartItemUseCase.invoke(sampleCartItem) } returns Unit
        viewModel.decrement(sampleCartItem)
        advanceUntilIdle()
        coVerify { decrementCartItemUseCase.invoke(sampleCartItem) }
    }

    @Test
    fun `remove calls clearCartUseCase`() = runTest {
        coEvery { removeCartItemUseCase.invoke(sampleCartItem) } returns Unit
        viewModel.remove(sampleCartItem)
        advanceUntilIdle()
        coVerify { removeCartItemUseCase.invoke(sampleCartItem) }
    }

    @Test
    fun `clearCart calls clearCartUseCase`() = runTest {
        coEvery { clearCartUseCase.invoke() } returns Unit
        viewModel.clearCart()
        advanceUntilIdle()
        coVerify { clearCartUseCase.invoke() }
    }
}