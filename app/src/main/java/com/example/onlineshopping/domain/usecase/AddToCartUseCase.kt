package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(product: Product) {
        cartRepository.addItem(product)
    }
}