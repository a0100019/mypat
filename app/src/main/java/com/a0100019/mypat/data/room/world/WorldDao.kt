package com.a0100019.mypat.data.room.world

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldDao {
    @Insert
    suspend fun insert(world: World)

    @Delete
    suspend fun delete(world: World)

    @Query("SELECT * FROM world_table ORDER BY id DESC")
    fun getAllWorldData(): Flow<List<World>>

    @Query("SELECT * FROM world_table WHERE id = :id")
    fun getWorldDataById(id: String): World

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<World>)
}