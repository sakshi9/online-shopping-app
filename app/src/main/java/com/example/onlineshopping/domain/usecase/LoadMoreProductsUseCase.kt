package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.data.model.ProductsResponse
import javax.inject.Inject

class LoadMoreProductsUseCase @Inject constructor(
    private val getProducts: GetProductsUseCase
) {
    suspend operator fun invoke(
        category: String,
        search: String,
        sort: String,
        currentPage: Int,
        totalPages: Int
    ): Result<ProductsResponse> {

        if (currentPage >= totalPages) {
            return Result.failure(Exception("No more pages"))
        }

        return getProducts(
            category = category,
            search = search,
            sort = sort,
            page = currentPage + 1
        )
    }
}