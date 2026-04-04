package com.example.onlineshopping.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CartEntity::class,
        ProductEntity::class,
        RemoteKeysEntity::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun productDao(): ProductDao

    abstract fun remoteKeyDao(): RemoteKeysDao
}