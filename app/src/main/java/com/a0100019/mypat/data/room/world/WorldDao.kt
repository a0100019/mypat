package com.a0100019.mypat.data.room.world

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(world: World)

    @Delete
    suspend fun delete(world: World)

    @Query("DELETE FROM world_table")
    suspend fun deleteAllWorlds()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'world_table'")
    suspend fun resetWorldPrimaryKey()

    @Update
    suspend fun update(item: World)

    @Query("UPDATE world_table SET situation = :newSituation WHERE id = :id")
    suspend fun updateSituationById(id: Int, newSituation: String)

    @Query("DELETE FROM world_table WHERE id != 1")
    suspend fun deleteAllExceptIdOne()

    @Query("SELECT * FROM world_table ORDER BY id DESC")
    fun getAllFlowWorldData(): Flow<List<World>>

    @Query("SELECT * FROM world_table")
    suspend fun getAllWorldData(): List<World>

    @Query("SELECT * FROM world_table WHERE id = :id")
    suspend fun getWorldDataById(id: Int): World

    @Query("SELECT * FROM world_table WHERE type = :type")
    suspend fun getWorldDataListByType(type: String): List<World>

    @Query("SELECT * FROM world_table WHERE type = :type")
    fun getFlowWorldDataListByType(type: String): Flow<List<World>>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<World>)
}