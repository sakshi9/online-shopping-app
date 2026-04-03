package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.data.model.Category
import com.example.onlineshopping.domain.repository.ProductRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(): Result<List<Category>> {
        return productRepository.getCategories()
    }
}