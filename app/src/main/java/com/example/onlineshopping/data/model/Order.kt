package com.example.onlineshopping.data.model

import com.example.onlineshopping.domain.model.CartData

data class Order(
    val id: String,
    val items: List<CartData>,
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double,
    val address: Address,
    val paymentMethod: String,
    val status: String,
    val estimatedDelivery: String,
    val placedAt: String
)

data class Address(
    val line1: String,
    val city: String,
    val postcode: String
)

