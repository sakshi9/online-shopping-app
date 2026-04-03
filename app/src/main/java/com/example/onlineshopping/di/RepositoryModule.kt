package com.example.onlineshopping.di

import com.example.onlineshopping.data.repository.ProductRepositoryImpl
import com.example.onlineshopping.data.repository.CartRepositoryImpl
import com.example.onlineshopping.domain.repository.ProductRepository
import com.example.onlineshopping.domain.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsUserDetailRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindsProductsRepository(impl: CartRepositoryImpl): CartRepository

}