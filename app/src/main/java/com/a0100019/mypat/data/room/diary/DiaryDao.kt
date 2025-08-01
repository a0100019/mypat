package com.a0100019.mypat.data.room.diary

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.a0100019.mypat.data.room.item.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diary: Diary)

    @Delete
    suspend fun delete(diary: Diary)

    @Query("DELETE FROM diary_table")
    suspend fun deleteAllDiary()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'diary_table'")
    suspend fun resetDiaryPrimaryKey()

    @Update
    suspend fun update(diary: Diary)

    @Query("SELECT * FROM diary_table ORDER BY id DESC")
    suspend fun getAllDiaryData(): List<Diary>

    @Query("SELECT * FROM diary_table ORDER BY id DESC")
    fun getAllFlowDiaryData(): Flow<List<Diary>>

    @Query("SELECT * FROM diary_table ORDER BY id DESC LIMIT 1")
    suspend fun getLatestDiary(): Diary

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diaries: List<Diary>)
}