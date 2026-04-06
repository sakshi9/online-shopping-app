package com.example.onlineshopping.viewModel

import app.cash.turbine.test
import com.example.onlineshopping.domain.model.HomeData
import com.example.onlineshopping.domain.usecase.GetHomeDataUseCase
import com.example.onlineshopping.ui.model.CheckoutUiState
import com.example.onlineshopping.ui.model.HomeUiState
import com.example.onlineshopping.ui.viewModel.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getHomeData: GetHomeDataUseCase = mockk()
    private lateinit var viewModel: HomeViewModel

    private val sampleHomeData = HomeData(
        categories = emptyList(),
        featured = emptyList(),
        deals = emptyList()
    )

    @Test
    fun `init calls loadHome and updates state on success`() = runTest {
        //Arrange
        coEvery { getHomeData() } returns Result.success(sampleHomeData)

        //Act
        viewModel = HomeViewModel(getHomeData)
        viewModel.uiState.test {
            // Assert
            assertEquals(HomeUiState(), awaitItem())

            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(sampleHomeData.categories, state.categories)
            assertNull(state.error)
        }
    }

    @Test
    fun `init calls loadHome and updates state on failure`() = runTest {
        val errorMessage = "Network Error"
        coEvery { getHomeData() } returns Result.failure(Exception(errorMessage))

        viewModel = HomeViewModel(getHomeData)

        viewModel.uiState.test {
            // Initial state
            assertEquals(HomeUiState(), awaitItem())

            // Error state
            val errorState = awaitItem()

            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.error)
        }
    }

    @Test
    fun `loadHome can be called manually to refresh`() = runTest {
        coEvery { getHomeData() } returns Result.success(sampleHomeData)
        viewModel = HomeViewModel(getHomeData)
        advanceUntilIdle()
        viewModel.uiState.test {
            val stateAfterInit = awaitItem()

            assertFalse(stateAfterInit.isLoading)
            viewModel.loadHome()
            assertTrue(awaitItem().isLoading)
            assertFalse(awaitItem().isLoading)
        }
    }
}