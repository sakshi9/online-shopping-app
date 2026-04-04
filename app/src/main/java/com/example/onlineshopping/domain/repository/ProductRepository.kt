package com.example.onlineshopping.domain.repository

import androidx.paging.PagingData
import com.example.onlineshopping.data.model.Address
import com.example.onlineshopping.data.model.Category
import com.example.onlineshopping.data.model.Order
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.ui.model.DeliverySlot
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getProductsPaged(
        category: String,
        search: String,
        sort: String
    ): Flow<PagingData<Product>>

    suspend fun getCategories(): Result<List<Category>>

    suspend fun getProductById(id: String): Result<Product>

    suspend fun getRelatedProducts(id: String): Result<List<Product>>

    suspend fun getFeaturedProducts(): Result<List<Product>>

    suspend fun getDeals(): Result<List<Product>>

    suspend fun searchSuggestions(query: String): Result<List<Product>>

    suspend fun placeOrder(
        items: List<CartData>, address: Address,
        paymentMethod: String, selectedSlot: DeliverySlot
    )
            : Result<Order>

    fun getFakeUser()
}