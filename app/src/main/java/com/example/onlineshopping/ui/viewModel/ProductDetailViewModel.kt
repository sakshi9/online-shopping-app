package com.example.onlineshopping.ui.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshopping.domain.usecase.AddToCartUseCase
import com.example.onlineshopping.domain.usecase.DecrementCartItemUseCase
import com.example.onlineshopping.domain.usecase.GetCartItemQuantityUseCase
import com.example.onlineshopping.domain.usecase.GetProductDetailUseCase
import com.example.onlineshopping.domain.usecase.IncrementCartItemUseCase
import com.example.onlineshopping.ui.model.ProductDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val getCartQtyUseCase: GetCartItemQuantityUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val incrementUseCase: IncrementCartItemUseCase,
    private val decrementUseCase: DecrementCartItemUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = checkNotNull(savedStateHandle["productId"])

    private val _state = MutableStateFlow(ProductDetailsUiState())
    val state: StateFlow<ProductDetailsUiState> = _state.asStateFlow()

    init {
        loadProduct()

        viewModelScope.launch {
            getCartQtyUseCase(productId).collect { qty ->
                _state.update { it.copy(cartQuantity = qty) }
            }
        }
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = getProductDetailUseCase(productId)

            result.onSuccess { data ->
                _state.update {
                    it.copy(
                        product = data.product,
                        related = data.related,
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(error = e.message, isLoading = false)
                }
            }
        }
    }

    fun addToCart() {
        val product = _state.value.product ?: return

        viewModelScope.launch {
            addToCartUseCase(product)
            _state.update { it.copy(addedToCart = true) }
        }
    }

    fun incrementQty() {
        viewModelScope.launch {
            incrementUseCase(productId)
        }
    }

    fun decrementQty() {
        viewModelScope.launch {
            decrementUseCase(productId)
        }
    }
}