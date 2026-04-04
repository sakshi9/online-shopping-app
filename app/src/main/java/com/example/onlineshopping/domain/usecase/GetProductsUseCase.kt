package com.example.onlineshopping.domain.usecase

import androidx.paging.PagingData
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(
        category: String = "all",
        search: String = "",
        sort: String = "default"
    ): Flow<PagingData<Product>> {
        return productRepository.getProductsPaged(category, search, sort)
    }
}