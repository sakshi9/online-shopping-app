package com.example.onlineshopping.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKeys: RemoteKeysEntity)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun remoteKeysById(id: String): RemoteKeysEntity?

    @Query("DELETE FROM remote_keys WHERE id = :id")
    suspend fun deleteById(id: String)
}