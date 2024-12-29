package com.a0100019.mypat.data.room.diary

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Insert
    suspend fun insert(diary: Diary)

    @Delete
    suspend fun delete(diary: Diary)

    @Query("SELECT * FROM diary_table ORDER BY id DESC")
    fun getAllDiaries(): Flow<List<Diary>>
}