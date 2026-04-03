package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend fun invoke() {
        return cartRepository.clearCart()
    }
}