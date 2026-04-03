package com.example.onlineshopping.ui.model

import com.example.onlineshopping.domain.model.CartData

data class CartUiState(
    val items: List<CartData> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 400.00,
    val total: Double = 0.0
)
