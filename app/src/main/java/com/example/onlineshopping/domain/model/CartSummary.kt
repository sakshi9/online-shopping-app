package com.example.onlineshopping.domain.model

data class CartSummary(
    val items: List<CartData>,
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double
)
