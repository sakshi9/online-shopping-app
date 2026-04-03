package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.domain.repository.ProductRepository
import javax.inject.Inject

class GetDealsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(): Result<List<Product>> {
        return productRepository.getDeals()
    }
}