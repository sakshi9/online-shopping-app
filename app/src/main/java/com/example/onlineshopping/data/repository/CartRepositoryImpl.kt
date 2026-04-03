package com.example.onlineshopping.data.repository

import com.example.onlineshopping.data.local.CartDao
import com.example.onlineshopping.data.local.CartEntity
import com.example.onlineshopping.data.mapper.toDomain
import com.example.onlineshopping.data.mapper.toEntity
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.data.remote.CartApi
import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi,
    private val cartDao: CartDao
) : CartRepository {

    override fun getCartItems(): Flow<List<CartData>> {
        return cartDao.getAll()
            .map { entities -> entities.map { it.toDomain() } }
    }

    val totalItemCount: Flow<Int?> = cartDao.getTotalItemCount()

    override suspend fun addItem(product: Product) {
        val existing = cartDao.getById(product.id)
        if (existing != null) {
            cartDao.update(existing.copy(quantity = existing.quantity + 1))
        } else {
            cartDao.insert(
                CartEntity(
                    product.id,
                    product.name,
                    product.price,
                    product.image,
                    product.unit,
                    1
                )
            )
        }
    }

    override suspend fun removeItem(item: CartData) {
        cartDao.delete(item.toEntity())
    }

    override suspend fun updateQuantity(
        item: CartData,
        quantity: Int
    ) {
        if (quantity <= 0) cartDao.delete(item.toEntity())
        else cartDao.update(item.toEntity().copy(quantity = quantity))
    }

    override suspend fun clearCart() {
        cartDao.clearAll()
    }
}