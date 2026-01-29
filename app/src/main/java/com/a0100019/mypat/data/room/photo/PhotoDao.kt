package com.a0100019.mypat.data.room.photo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.english.English
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo)

    @Delete
    suspend fun delete(photo: Photo)

    @Query("DELETE FROM photo_table")
    suspend fun deleteAllPhoto()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'photo_table'")
    suspend fun resetPhotoPrimaryKey()

    @Update
    suspend fun update(photo: Photo)

    @Query("SELECT * FROM photo_table ORDER BY id DESC")
    suspend fun getAllPhotoData(): List<Photo>

    @Query("SELECT * FROM photo_table ORDER BY id DESC")
    fun getAllFlowPhotoData(): Flow<List<Photo>>

    // ✅ 특정 날짜의 사진 리스트 가져오기 (날짜가 일치하는 것들만)
    @Query("SELECT * FROM photo_table WHERE date = :date ORDER BY id ASC")
    suspend fun getPhotosByDate(date: String): List<Photo>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<Photo>)
}