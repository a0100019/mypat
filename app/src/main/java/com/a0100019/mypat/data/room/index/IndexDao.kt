package com.a0100019.mypat.data.room.index

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.a0100019.mypat.data.room.english.English
import kotlinx.coroutines.flow.Flow

@Dao
interface IndexDao {
    @Insert
    suspend fun insert(index: Index)

    @Delete
    suspend fun delete(index: Index)

    @Query("SELECT * FROM index_table ORDER BY id DESC")
    fun getAllIndex(): Flow<List<Index>>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<Index>)
}