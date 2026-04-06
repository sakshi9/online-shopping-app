package com.example.onlineshopping.viewModel

import androidx.paging.PagingData
import app.cash.turbine.test
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.domain.usecase.GetProductsUseCase
import com.example.onlineshopping.domain.usecase.GetSearchSuggestionsUseCase
import com.example.onlineshopping.ui.viewModel.ShopViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShopViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getProductsUseCase: GetProductsUseCase = mockk()
    private val getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase = mockk()

    private lateinit var shopViewModel: ShopViewModel

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

    @Before
    fun setup() {
        every { getProductsUseCase(any(), any(), any()) } returns flowOf(PagingData.empty())
        shopViewModel = ShopViewModel(getProductsUseCase, getSearchSuggestionsUseCase)
    }

    @Test
    fun `suggestions flow returns empty list immediately for blank query`() = runTest {
        shopViewModel.suggestions.test {
            assertEquals(emptyList<Product>(), awaitItem())
            shopViewModel.onSearchQueryChange("   ")

            advanceTimeBy(301)
            coVerify(exactly = 0) { getSearchSuggestionsUseCase(any()) }
        }
    }

    @Test
    fun `suggestions flow uses debounce and calls usecase for non-blank query`() = runTest {
        val query = "apple"
        val mockSuggestions = listOf(product)
        coEvery { getSearchSuggestionsUseCase(query) } returns Result.success(mockSuggestions)

        shopViewModel.suggestions.test {
            assertEquals(emptyList<Product>(), awaitItem())

            shopViewModel.onSearchQueryChange(query)

            advanceTimeBy(200)

            assertEquals(mockSuggestions, awaitItem())
            coVerify(exactly = 1) { getSearchSuggestionsUseCase(query) }
        }
    }

    @Test
    fun `products flow reacts to state changes`() = runTest {
        shopViewModel.products.test {
            // Initial call with defaults
            awaitItem()
            coVerify { getProductsUseCase("all", "", "default") }

            // Change category
            shopViewModel.setCategory("fruits")
            awaitItem()
            coVerify { getProductsUseCase("fruits", "", "default") }

            // Change sort
            shopViewModel.setSort("price_desc")
            awaitItem()
            coVerify { getProductsUseCase("fruits", "", "price_desc") }
        }
    }
}