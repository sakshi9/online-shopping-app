package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCheckoutSummaryUseCase @Inject constructor(
    private val cartRepo: CartRepository
) {
    operator fun invoke(): Flow<CheckoutSummary> {
        return cartRepo.getCartItems().map { items ->

            val subtotal = items.sumOf { it.price * it.quantity }
            val deliveryFee = if (subtotal >= 400.0) 0.0 else 20.0

            CheckoutSummary(
                items = items,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                total = subtotal + deliveryFee
            )
        }
    }
}

data class CheckoutSummary(
    val items: List<CartData>,
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double
)