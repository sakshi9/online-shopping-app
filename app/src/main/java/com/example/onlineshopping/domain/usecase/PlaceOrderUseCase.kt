package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.data.model.Address
import com.example.onlineshopping.data.model.Order
import com.example.onlineshopping.domain.repository.CartRepository
import com.example.onlineshopping.domain.repository.ProductRepository
import com.example.onlineshopping.ui.model.DeliverySlot
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val cartRepo: CartRepository,
    private val productRepo: ProductRepository
) {
    suspend operator fun invoke(
        address: Address,
        paymentMethod: String,
        selectedSlot : DeliverySlot
    ): Result<Order> {

        val items = cartRepo.getCartItems().first()

        if (items.isEmpty()) {
            return Result.failure(Exception("Cart is empty"))
        }

        return productRepo.placeOrder(items, address, paymentMethod, selectedSlot).onSuccess {
                cartRepo.clearCart()
            }
    }
}