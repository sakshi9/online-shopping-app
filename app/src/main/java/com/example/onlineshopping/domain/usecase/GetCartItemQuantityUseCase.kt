package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartItemQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(productId: String): Flow<Int> {
        return cartRepository.getCartItems().map { items ->
            items.find { it.productId == productId }?.quantity ?: 0
        }
    }
}