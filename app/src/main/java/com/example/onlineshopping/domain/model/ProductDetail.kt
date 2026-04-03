package com.example.onlineshopping.domain.model

import com.example.onlineshopping.data.model.Product

data class ProductDetail(
    val product: Product,
    val related: List<Product>
)
