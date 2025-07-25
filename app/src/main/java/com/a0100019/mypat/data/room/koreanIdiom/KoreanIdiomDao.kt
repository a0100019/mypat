package com.a0100019.mypat.data.room.koreanIdiom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface KoreanIdiomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(koreanIdiom: KoreanIdiom)

    @Delete
    suspend fun delete(koreanIdiom: KoreanIdiom)

    @Query("DELETE FROM koreanIdiom_table")
    suspend fun deleteAllKoreanIdioms()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'koreanIdiom_table'")
    suspend fun resetKoreanIdiomPrimaryKey()

    @Update
    suspend fun update(koreanIdiom: KoreanIdiom)

    @Query("UPDATE koreanIdiom_table SET date = :date, state = :state WHERE id = :id")
    suspend fun updateDateAndState(id: Int, date: String, state: String)

    @Query("SELECT * FROM koreanIdiom_table ORDER BY id DESC")
    suspend fun getAllKoreanIdiomData(): List<KoreanIdiom>

    @Query("SELECT * FROM koreanIdiom_table WHERE state != '미정' ORDER BY id DESC")
    suspend fun getOpenKoreanIdiomData(): List<KoreanIdiom>

    @Query("SELECT * FROM koreanIdiom_table WHERE state = '미정' ORDER BY id LIMIT 1")
    suspend fun getCloseKoreanIdiom(): KoreanIdiom?

    @Query("SELECT * FROM koreanIdiom_table WHERE state = '별' ORDER BY id DESC")
    suspend fun getStarKoreanIdiomData(): List<KoreanIdiom>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<KoreanIdiom>)
}