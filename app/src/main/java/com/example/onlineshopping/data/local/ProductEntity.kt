package com.example.onlineshopping.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val cacheKey: String,
    val productId: String,
    val name: String,
    val category: String,
    val price: Double,
    val unit: String,
    val image: String,
    val badge: String?,
    val rating: Double,
    val reviews: Int,
    val inStock: Boolean,
    val description: String,
    val filterCategory: String,
    val filterSearch: String,
    val filterSort: String,
    val page: Int
)
