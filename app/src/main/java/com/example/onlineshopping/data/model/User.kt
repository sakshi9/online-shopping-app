package com.example.onlineshopping.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val clubcard: String,
    val points: Int,
    val address: Address
)
