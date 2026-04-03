package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.data.model.ProductsResponse
import com.example.onlineshopping.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        category: String,
        search: String,
        sort: String,
        page: Int
    ): Result<ProductsResponse> {
        return productRepository.getProducts(category, search, sort, page)
    }
}