package com.example.onlineshopping.ui.model

import com.example.onlineshopping.data.model.Product

data class ProductDetailsUiState(
    val product: Product? = null,
    val related: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val cartQuantity: Int = 0,
    val addedToCart: Boolean = false
)
