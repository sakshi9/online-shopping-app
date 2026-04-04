package com.example.onlineshopping.data.mapper

import com.example.onlineshopping.data.local.ProductEntity
import com.example.onlineshopping.data.model.Product

fun Product.ToEntity(
    category: String,
    search: String,
    sort: String,
    page: Int
): ProductEntity {
    return ProductEntity(
        cacheKey = "$id-$category-$search-$sort",
        productId = id,
        name = name,
        category = this.category,
        price = price,
        unit = unit,
        image = image,
        badge = badge,
        rating = rating,
        reviews = reviews,
        inStock = inStock,
        description = description,
        filterCategory = category,
        filterSearch = search,
        filterSort = sort,
        page = page
    )
    }

