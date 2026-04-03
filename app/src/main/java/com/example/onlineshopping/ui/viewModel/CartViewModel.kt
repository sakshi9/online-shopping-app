package com.example.onlineshopping.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.domain.usecase.ClearCartUseCase
import com.example.onlineshopping.domain.usecase.DecrementCartItemUseCase
import com.example.onlineshopping.domain.usecase.GetCartUseCase
import com.example.onlineshopping.domain.usecase.IncrementCartItemUseCase
import com.example.onlineshopping.domain.usecase.RemoveCartItemUseCase
import com.example.onlineshopping.ui.model.CartUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val incrementCartItemUseCase: IncrementCartItemUseCase,
    private val decrementCartItemUseCase: DecrementCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : ViewModel() {

    val uiState: StateFlow<CartUiState> = getCartUseCase.invoke().map { items ->
        val subtotal = items.sumOf { it.price * it.quantity }
        val deliveryFee = if (subtotal >= 400.0) 0.0 else 40.0
        CartUiState(
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            total = subtotal + deliveryFee
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CartUiState()
    )

    fun increment(item: CartData) {
        viewModelScope.launch { incrementCartItemUseCase.invoke(item) }
    }

    fun decrement(item: CartData) {
        viewModelScope.launch { decrementCartItemUseCase.invoke(item) }
    }

    fun remove(item: CartData) {
        viewModelScope.launch { removeCartItemUseCase.invoke(item) }
    }

    fun clearCart() {
        viewModelScope.launch { clearCartUseCase.invoke() }
    }
}