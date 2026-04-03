package com.example.onlineshopping.domain.model

data class CartData(
    val productId: String,
    val name: String,
    val price: Double,
    val image: String,
    val unit: String,
    var quantity: Int
)
