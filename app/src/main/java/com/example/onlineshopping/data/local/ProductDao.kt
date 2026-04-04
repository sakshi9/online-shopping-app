package com.example.onlineshopping.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE filterCategory = :category AND filterSearch = :search AND filterSort = :sort ORDER BY page ASC")
    fun getProductsPaged(
        category: String,
        search: String,
        sort: String
    ): PagingSource<Int, ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query(" DELETE FROM products WHERE filterCategory = :category AND filterSearch = :search AND filterSort = :sort")
    suspend fun clearByFilter(category: String, search: String, sort: String)
}

