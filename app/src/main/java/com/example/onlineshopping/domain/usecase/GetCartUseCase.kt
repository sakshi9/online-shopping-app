package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val cartRepository: CartRepository) {

    operator fun invoke(): Flow<List<CartData>> {
        return cartRepository.getCartItems()
    }
}