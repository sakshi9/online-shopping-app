package com.example.onlineshopping.data.remote

data class CartDto(
    val id: String,
    val items: List<CartDto>,
    val subtotal: Double,
    val deliveryFee: Double
)
