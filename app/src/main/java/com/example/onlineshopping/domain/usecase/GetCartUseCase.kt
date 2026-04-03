package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.domain.model.CartSummary
import com.example.onlineshopping.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val cartRepository: CartRepository) {

    operator fun invoke(): Flow<CartSummary> {
        return cartRepository.getCartItems().map { items ->
            val subtotal = items.sumOf { it.price * it.quantity }
            val deliveryFee = if (subtotal >= 400.0) 0.0 else 40.0

            CartSummary(
                items = items,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                total = subtotal + deliveryFee
            )
        }
    }
}