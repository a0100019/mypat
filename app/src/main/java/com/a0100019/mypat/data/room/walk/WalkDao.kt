package com.a0100019.mypat.data.room.walk

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.a0100019.mypat.data.room.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkDao {
    @Insert
    suspend fun insert(walk: Walk)

    @Delete
    suspend fun delete(walk: Walk)

    @Query("SELECT * FROM walk_table ORDER BY id DESC")
    suspend fun getAllWalkData(): List<Walk>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(walks: List<Walk>)
}