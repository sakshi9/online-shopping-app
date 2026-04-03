package com.example.onlineshopping.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.onlineshopping.data.remote.FakeGroceryApi
import com.example.onlineshopping.data.model.Address
import com.example.onlineshopping.data.model.Category
import com.example.onlineshopping.data.model.Order
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.data.model.ProductsResponse
import com.example.onlineshopping.data.remote.ProductApi
import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.domain.repository.ProductRepository
import com.example.onlineshopping.ui.model.DeliverySlot
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: FakeGroceryApi,
    private val productApi: ProductApi
) : ProductRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val categories = api.getCategories()
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProducts(
        category: String,
        search: String,
        sort: String,
        page: Int
    ) : Result<ProductsResponse> {
        return try {
            val products = api.getProducts(category, search, sort, page)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductById(id: String) : Result<Product> {
        return try {
            val product = api.getProductById(id)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRelatedProducts(id: String) : Result<List<Product>> {
        return try {
            val relatedProduct =  api.getRelatedProducts(id)
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

    override suspend fun searchSuggestions(query: String) : Result<List<Product>> {
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
        selectedSlot : DeliverySlot
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