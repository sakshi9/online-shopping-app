package com.example.onlineshopping.domain.usecase

import com.example.onlineshopping.domain.model.HomeData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetHomeDataUseCase  @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getFeaturedUseCase: GetFeaturedProductsUseCase,
    private val getDealsUseCase: GetDealsUseCase)  {

    suspend operator fun invoke(): Result<HomeData> {
        return try {
            coroutineScope {
                val categoriesDeferred = async { getCategoriesUseCase() }
                val featuredDeferred = async { getFeaturedUseCase() }
                val dealsDeferred = async { getDealsUseCase() }

                val categories = categoriesDeferred.await().getOrDefault(emptyList())
                val featured = featuredDeferred.await().getOrDefault(emptyList())
                val deals = dealsDeferred.await().getOrDefault(emptyList())

                Result.success(
                    HomeData(
                        categories = categories,
                        featured = featured,
                        deals = deals
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}