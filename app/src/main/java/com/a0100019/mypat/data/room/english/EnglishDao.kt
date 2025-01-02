package com.a0100019.mypat.data.room.english

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.a0100019.mypat.data.room.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface EnglishDao {
    @Insert
    suspend fun insert(english: English)

    @Delete
    suspend fun delete(english: English)

    @Query("SELECT * FROM english_table ORDER BY id DESC")
    fun getAllEnglishData(): Flow<List<English>>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<English>)
}