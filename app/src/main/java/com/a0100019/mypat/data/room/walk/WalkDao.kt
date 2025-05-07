package com.a0100019.mypat.data.room.walk

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.a0100019.mypat.data.room.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(walk: Walk)

    @Delete
    suspend fun delete(walk: Walk)

    @Query("DELETE FROM walk_table")
    suspend fun deleteAllWalks()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'walk_table'")
    suspend fun resetWalkPrimaryKey()

    @Update
    suspend fun update(walk: Walk)

    @Query("UPDATE walk_table SET count = :newCount WHERE date = :date")
    suspend fun updateCountByDate(date: String, newCount: Int)

    @Query("UPDATE walk_table SET steps = :newSteps WHERE date = :date")
    suspend fun updateStepsByDate(date: String, newSteps: Int)

    @Query("SELECT * FROM walk_table ORDER BY id DESC")
    suspend fun getAllWalkData(): List<Walk>

    @Query("SELECT * FROM walk_table ORDER BY id DESC LIMIT 1")
    suspend fun getLatestWalkData(): Walk

    @Query("SELECT * FROM walk_table ORDER BY id DESC LIMIT 1 OFFSET 1")
    suspend fun getSecondLatestWalkData(): Walk

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(walks: List<Walk>)
}