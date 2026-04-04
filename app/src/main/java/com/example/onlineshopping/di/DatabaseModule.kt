package com.example.onlineshopping.di

import android.content.Context
import androidx.room.Room
import com.example.onlineshopping.data.local.AppDatabase
import com.example.onlineshopping.data.local.CartDao
import com.example.onlineshopping.data.local.ProductDao
import com.example.onlineshopping.data.local.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "cart_db"

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCartDao(
        database: AppDatabase
    ): CartDao {
        return database.cartDao()
    }

    @Provides
    fun provideProductDao(
        database: AppDatabase
    ): ProductDao {
        return database.productDao()
    }

    @Provides
    fun provideRemoteKeyDao(
        database: AppDatabase
    ): RemoteKeysDao {
        return database.remoteKeyDao()
    }
}