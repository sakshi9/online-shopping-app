package com.example.onlineshopping.ui.model

import com.example.onlineshopping.data.model.Product

data class ShopUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val total: Int = 0,
    val selectedCategory: String = "all",
    val searchQuery: String = "",
    val sortBy: String = "default",
    val suggestions: List<Product> = emptyList()
)
