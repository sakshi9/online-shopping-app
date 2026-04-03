package com.example.onlineshopping.di

import android.content.Context
import androidx.room.Room
import com.example.onlineshopping.data.local.CartDao
import com.example.onlineshopping.data.local.CartDb
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
    ): CartDb {
        return Room.databaseBuilder(
            context,
            CartDb::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCartDao(
        database: CartDb
    ): CartDao {
        return database.cartDao()
    }
}