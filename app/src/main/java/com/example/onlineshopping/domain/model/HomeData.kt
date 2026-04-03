package com.example.onlineshopping.domain.model

import com.example.onlineshopping.data.model.Category
import com.example.onlineshopping.data.model.Product

data class HomeData(
    val categories: List<Category>,
    val featured: List<Product>,
    val deals: List<Product>
)
