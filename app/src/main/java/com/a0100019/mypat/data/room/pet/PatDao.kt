package com.a0100019.mypat.data.room.pet

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PatDao {
    @Insert
    suspend fun insert(pat: Pat)

    @Delete
    suspend fun delete(pat: Pat)

    @Update
    suspend fun update(item: Pat)

    @Query("SELECT * FROM pat_table ORDER BY id DESC")
    suspend fun getAllPatData(): List<Pat>

    //flow 아닐 때 suspend함수 필수!!!
    @Query("SELECT * FROM pat_table WHERE id = :id")
    suspend fun getPatDataById(id: String): Pat

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<Pat>)



    //room 데이터를 실시간으로 감시하려면 flow나 livedata를 사용해야 한다.
}