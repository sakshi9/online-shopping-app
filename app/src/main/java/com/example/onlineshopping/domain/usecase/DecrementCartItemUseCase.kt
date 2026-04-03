package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.domain.repository.CartRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DecrementCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository) {

    suspend operator fun invoke(productId: String) {
        val item = cartRepository.getCartItems().first()
            .find { it.productId == productId }

        if (item != null) {
            handle(item)
        }
    }

    suspend operator fun invoke(item: CartData) {
        handle(item)
    }

    private suspend fun handle(item: CartData) {
        if (item.quantity > 1) {
            cartRepository.updateQuantity(item, item.quantity - 1)
        } else {
            cartRepository.removeItem(item)
        }
    }
}