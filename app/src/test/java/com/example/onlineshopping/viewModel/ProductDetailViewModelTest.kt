package com.example.onlineshopping.viewModel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.domain.model.ProductDetail
import com.example.onlineshopping.domain.usecase.AddToCartUseCase
import com.example.onlineshopping.domain.usecase.DecrementCartItemUseCase
import com.example.onlineshopping.domain.usecase.GetCartItemQuantityUseCase
import com.example.onlineshopping.domain.usecase.GetProductDetailUseCase
import com.example.onlineshopping.domain.usecase.IncrementCartItemUseCase
import com.example.onlineshopping.ui.model.ProductDetailsUiState
import com.example.onlineshopping.ui.viewModel.ProductDetailViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getProductDetailUseCase: GetProductDetailUseCase = mockk()
    private val getCartQtyUseCase: GetCartItemQuantityUseCase = mockk()
    private val addToCartUseCase: AddToCartUseCase = mockk()
    private val incrementUseCase: IncrementCartItemUseCase = mockk()
    private val decrementUseCase: DecrementCartItemUseCase = mockk()

    private lateinit var productDetailViewModel: ProductDetailViewModel

    private val product = Product(
        id = "p123",
        "Test Product",
        "Test Category",
        10.0,
        "kg",
        "url",
        null,
        4.5,
        10,
        true,
        "desc"
    )
    private val productDetail = ProductDetail(product, emptyList())

    private fun createViewModel() {
        productDetailViewModel = ProductDetailViewModel(
            getProductDetailUseCase,
            getCartQtyUseCase,
            addToCartUseCase,
            incrementUseCase,
            decrementUseCase,
            SavedStateHandle(mapOf("productId" to "p123"))
        )
    }

    @Before
    fun setup() {
        every { getCartQtyUseCase.invoke("p123") } returns flowOf(2)
        coEvery { getProductDetailUseCase("p123") } returns Result.success(productDetail)
    }

    @Test
    fun `init load products and update state`() = runTest {
        createViewModel()

        productDetailViewModel.state.test {
            assertEquals(ProductDetailsUiState(), awaitItem())

            val state = awaitItem()
            assertEquals(productDetail.product, state.product)
            assertEquals(productDetail.related, state.related)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `load products failure and update error state`() = runTest {
        val errorMsg = "Not found"
        coEvery { getProductDetailUseCase("p123") } returns Result.failure(Exception(errorMsg))
        createViewModel()

        productDetailViewModel.state.test {
            assertEquals(ProductDetailsUiState(), awaitItem())

            val state = awaitItem()
            assertEquals(errorMsg, state.error)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `addToCart calls usecase and updates addedToCart`() = runTest {
        coEvery { addToCartUseCase(product) } returns Unit
        createViewModel()
        advanceUntilIdle()

        productDetailViewModel.addToCart()
        advanceUntilIdle()

        productDetailViewModel.state.test {

            assertEquals(ProductDetailsUiState(), awaitItem())

            val state = awaitItem()
            assertTrue(state.addedToCart)
        }
        coVerify { addToCartUseCase(product) }
    }

    @Test
    fun `incrementQty calls usecase`() = runTest {
        coEvery { incrementUseCase("p123") } returns Unit
        createViewModel()

        productDetailViewModel.incrementQty()
        advanceUntilIdle()

        coVerify { incrementUseCase("p123") }
    }

    @Test
    fun `decrementQty calls usecase`() = runTest {
        coEvery { decrementUseCase("p123") } returns Unit
        createViewModel()
        productDetailViewModel.decrementQty()

        advanceUntilIdle()

        coVerify { decrementUseCase("p123") }
    }
}