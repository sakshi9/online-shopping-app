package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.domain.repository.CartRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IncrementCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
){
    suspend operator fun invoke(productId: String) {
        val item = cartRepository.getCartItems().first()
            .find { it.productId == productId }

        if (item != null) {
            cartRepository.updateQuantity(item, item.quantity + 1)
        }
    }

    suspend operator fun invoke(item : CartData) {
        return cartRepository.updateQuantity(item, item.quantity + 1)
    }
}