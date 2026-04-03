package com.example.onlineshopping.domain.repository

import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.domain.model.CartData
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getCartItems(): Flow<List<CartData>>

    suspend fun addItem(product: Product)

    suspend fun removeItem(item: CartData)

    suspend fun updateQuantity(item: CartData, quantity: Int)

    suspend fun clearCart()
}