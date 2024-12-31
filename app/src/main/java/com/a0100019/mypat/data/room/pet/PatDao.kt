package com.a0100019.mypat.data.room.pet

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatDao {
    @Insert
    suspend fun insert(pat: Pat)

    @Delete
    suspend fun delete(pat: Pat)

    @Query("SELECT * FROM pat_table ORDER BY id DESC")
    fun getAllIndexes(): Flow<List<Pat>>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<Pat>)
}