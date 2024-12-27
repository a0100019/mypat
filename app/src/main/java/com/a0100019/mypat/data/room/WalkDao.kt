package com.a0100019.mypat.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkDao {
    @Insert
    suspend fun insert(walk: Walk)

    @Delete
    suspend fun delete(walk: Walk)

    @Query("SELECT * FROM walk_table ORDER BY id DESC")
    fun getAllWalks(): Flow<List<Walk>>
}