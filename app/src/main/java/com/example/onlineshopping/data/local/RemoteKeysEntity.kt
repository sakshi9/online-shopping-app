package com.example.onlineshopping.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey val id: String,
    val prevPage: Int?,
    val nextPage: Int?,
    val lastUpdated: Long = System.currentTimeMillis()
)
