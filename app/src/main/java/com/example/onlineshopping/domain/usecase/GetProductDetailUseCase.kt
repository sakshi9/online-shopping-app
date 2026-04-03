package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.domain.model.ProductDetail
import com.example.onlineshopping.domain.repository.ProductRepository
import javax.inject.Inject
import kotlin.collections.emptyList

class GetProductDetailUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String): Result<ProductDetail> {
        return try {
            val productResult = productRepository.getProductById(productId)

            if (productResult.isFailure) {
                return Result.failure(productResult.exceptionOrNull()!!)
            }

            val product = productResult.getOrThrow()

            val related = productRepository
                .getRelatedProducts(productId)
                .getOrDefault(emptyList())

            Result.success(
                ProductDetail(
                    product = product,
                    related = related
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}