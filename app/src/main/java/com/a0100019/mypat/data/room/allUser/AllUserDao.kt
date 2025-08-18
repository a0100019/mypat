package com.a0100019.mypat.data.room.allUser

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AllUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 기존 데이터 덮어쓰기
    suspend fun insert(allUser: AllUser)

    @Delete
    suspend fun delete(allUser: AllUser)

    @Query("DELETE FROM allUser_table")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM allUser_table WHERE ban = '0' ORDER BY lastLogIn DESC")
    suspend fun getAllUserDataNoBan(): List<AllUser>

    @Query("UPDATE allUser_table SET `like` = :newLike WHERE tag = :tag")
    suspend fun updateLikeByTag(tag: String, newLike: String)

    @Update
    suspend fun update(allUser: AllUser)

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(allUsers: List<AllUser>)

}