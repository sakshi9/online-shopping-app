package com.example.onlineshopping.data.mapper

import com.example.onlineshopping.data.local.CartEntity
import com.example.onlineshopping.domain.model.CartData

fun CartEntity.toDomain(): CartData {
    return CartData(
        productId = productId,
        name = name,
        price = price,
        image = image,
        unit = unit,
        quantity = quantity
    )
}