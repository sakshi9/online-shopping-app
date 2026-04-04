package com.example.onlineshopping.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.onlineshopping.data.local.AppDatabase
import com.example.onlineshopping.data.local.ProductDao
import com.example.onlineshopping.data.local.RemoteKeysDao
import com.example.onlineshopping.data.mapper.toProduct
import com.example.onlineshopping.data.remote.FakeGroceryApi
import com.example.onlineshopping.data.model.Address
import com.example.onlineshopping.data.model.Category
import com.example.onlineshopping.data.model.Order
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.data.remote.ProductApi
import com.example.onlineshopping.data.remote.ProductRemoteMediator
import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.domain.repository.ProductRepository
import com.example.onlineshopping.ui.model.DeliverySlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: FakeGroceryApi,
    private val productApi: ProductApi,
    private val productDao: ProductDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val db: AppDatabase
) : ProductRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getProductsPaged(
        category: String,
        search: String,
        sort: String
    ): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 12,
                prefetchDistance = 4,
                enablePlaceholders = false
            ),
            remoteMediator = ProductRemoteMediator(
                category = category,
                search = search,
                sort = sort,
                api = api,
                productDao = productDao,
                remoteKeysDao = remoteKeysDao,
                db = db
            ),
            pagingSourceFactory = {
                productDao.getProductsPaged(
                    category = category,
                    search = search,
                    sort = sort
                )
            }
        )
            .flow.map { pagingData -> pagingData.map { it.toProduct() } }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val categories = api.getCategories()
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductById(id: String): Result<Product> {
        return try {
            val product = api.getProductById(id)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRelatedProducts(id: String): Result<List<Product>> {
        return try {
            val relatedProduct = api.getRelatedProducts(id)
            Result.success(relatedProduct)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun getFeaturedProducts(): Result<List<Product>> {
        return try {
            val featuredProducts = api.getFeaturedProducts()
            Result.success(featuredProducts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDeals(): Result<List<Product>> {
        return try {
            val deals = api.getDeals()
            Result.success(deals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchSuggestions(query: String): Result<List<Product>> {
        return try {
            val searchSuggestionList = api.searchSuggestions(query)
            Result.success(searchSuggestionList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun placeOrder(
        items: List<CartData>,
        address: Address,
        paymentMethod: String,
        selectedSlot: DeliverySlot
    ): Result<Order> {
        return try {
            val order = api.placeOrder(items, address, paymentMethod, selectedSlot)
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFakeUser() {
        api.getFakeUser()
    }
}