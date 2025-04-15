package com.a0100019.mypat.data.room.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)

    //value 중 원하는 값 변경
    @Query("""
    UPDATE user_table 
    SET 
        value = CASE WHEN :value IS NOT NULL THEN :value ELSE value END,
        value2 = CASE WHEN :value2 IS NOT NULL THEN :value2 ELSE value2 END,
        value3 = CASE WHEN :value3 IS NOT NULL THEN :value3 ELSE value3 END
    WHERE id = :id
        """)
    suspend fun update(id: String, value: String? = null, value2: String? = null, value3: String? = null)

    @Update
    suspend fun updateUsers(users: List<User>)

    @Query("SELECT value FROM user_table WHERE id = :id")
    suspend fun getValueById(id: String): String

    @Query("""
        SELECT *
        FROM user_table
        ORDER BY id DESC
        """)
    suspend fun getAllUserData(): List<User>

    //Flow는 이미 계속 비동기로 상태를 관찰하기 때문에 비동기함수인 suspend를 붙히면 안됨
    @Query("""
    SELECT *
    FROM user_table
    ORDER BY id DESC
        """)
    fun getAllUserDataFlow(): Flow<List<User>>


    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

}