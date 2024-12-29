package com.a0100019.mypat.data.room.koreanIdiom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.a0100019.mypat.data.room.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface KoreanIdiomDao {
    @Insert
    suspend fun insert(koreanIdiom: KoreanIdiom)

    @Delete
    suspend fun delete(koreanIdiom: KoreanIdiom)

    @Query("SELECT * FROM koreanIdiom_table ORDER BY id DESC")
    fun getAllKoreanIdioms(): Flow<List<KoreanIdiom>>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<KoreanIdiom>)
}