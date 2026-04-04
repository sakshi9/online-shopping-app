package com.example.onlineshopping.data.mapper

import com.example.onlineshopping.data.local.ProductEntity
import com.example.onlineshopping.data.model.Product

fun ProductEntity.toProduct() = Product(
    id = productId,
    name = name,
    category = category,
    price = price,
    unit = unit,
    image = image,
    badge = badge,
    rating = rating,
    reviews = reviews,
    inStock = inStock,
    description = description
)
