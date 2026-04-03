package com.example.onlineshopping.domain.repository

import com.example.onlineshopping.data.model.Address
import com.example.onlineshopping.data.model.Category
import com.example.onlineshopping.data.model.Order
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.data.model.ProductsResponse
import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.ui.model.DeliverySlot

interface ProductRepository {

    suspend fun getCategories(): Result<List<Category>>

    suspend fun getProducts(
        category: String = "all", search: String = "",
        sort: String = "default", page: Int = 1
    ): Result<ProductsResponse>

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