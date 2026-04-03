package com.example.onlineshopping.ui.model

import com.example.onlineshopping.data.model.Category
import com.example.onlineshopping.data.model.Product

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val featured: List<Product> = emptyList(),
    val deals: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
