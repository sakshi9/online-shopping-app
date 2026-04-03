package com.example.onlineshopping.data.model

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val unit: String,
    val image: String,
    val badge: String?,
    val rating: Double,
    val reviews: Int,
    val inStock: Boolean,
    val description: String = ""
)
