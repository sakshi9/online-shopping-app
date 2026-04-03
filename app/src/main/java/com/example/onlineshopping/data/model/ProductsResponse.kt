package com.example.onlineshopping.data.model

data class ProductsResponse(
    val items: List<Product>,
    val total: Int,
    val page: Int,
    val totalPages: Int
)
