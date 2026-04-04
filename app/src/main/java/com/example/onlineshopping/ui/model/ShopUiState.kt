package com.example.onlineshopping.ui.model

import com.example.onlineshopping.data.model.Product

data class ShopUiState(
    val selectedCategory: String = "all",
    val searchQuery: String = "",
    val sortBy: String = "default",
    val suggestions: List<Product> = emptyList()
)
