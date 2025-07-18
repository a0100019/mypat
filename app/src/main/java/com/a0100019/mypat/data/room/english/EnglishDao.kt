package com.a0100019.mypat.data.room.english

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.data.room.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface EnglishDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(english: English)

    @Delete
    suspend fun delete(english: English)

    @Query("DELETE FROM english_table")
    suspend fun deleteAllEnglish()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'english_table'")
    suspend fun resetEnglishPrimaryKey()

    @Update
    suspend fun update(english: English)

    @Query("UPDATE english_table SET date = :date, state = :state WHERE id = :id")
    suspend fun updateDateAndState(id: Int, date: String, state: String)

    @Query("SELECT * FROM english_table ORDER BY id DESC")
    suspend fun getAllEnglishData(): List<English>

    @Query("SELECT * FROM english_table WHERE state != '미정' ORDER BY id DESC")
    suspend fun getOpenEnglishData(): List<English>

    @Query("SELECT * FROM english_table WHERE state = '미정' ORDER BY id LIMIT 1")
    suspend fun getCloseEnglish(): English?

    @Query("SELECT * FROM english_table WHERE state = '별' ORDER BY id DESC")
    suspend fun getStarEnglishData(): List<English>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<English>)
}