package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.domain.repository.CartRepository
import javax.inject.Inject

class RemoveCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend fun invoke(item: CartData) {
        return cartRepository.removeItem(item)
    }
}