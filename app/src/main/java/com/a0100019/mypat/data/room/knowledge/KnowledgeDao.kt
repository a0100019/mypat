package com.a0100019.mypat.data.room.knowledge

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface KnowledgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(knowledge: Knowledge)

    @Query("SELECT COUNT(*) FROM knowledge_table")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(list: List<Knowledge>)

    @Delete
    suspend fun delete(knowledge: Knowledge)

    @Query("DELETE FROM knowledge_table")
    suspend fun deleteAllKnowledge()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'knowledge_table'")
    suspend fun resetKnowledgePrimaryKey()

    @Update
    suspend fun update(knowledge: Knowledge)

    @Query("UPDATE knowledge_table SET date = :date, state = :state WHERE id = :id")
    suspend fun updateDateAndState(id: Int, date: String, state: String)

    @Query("SELECT * FROM knowledge_table ORDER BY id DESC")
    suspend fun getAllKnowledgeData(): List<Knowledge>

    @Query("SELECT * FROM knowledge_table WHERE state != '미정' ORDER BY id DESC")
    suspend fun getOpenKnowledgeData(): List<Knowledge>

    @Query("SELECT * FROM knowledge_table WHERE state = '미정' ORDER BY id LIMIT 1")
    suspend fun getCloseKnowledge(): Knowledge?

    @Query("""
    SELECT EXISTS(
        SELECT 1 
        FROM knowledge_table 
        WHERE date = :date
    )
""")
    suspend fun existsByDate(date: String): Boolean

    @Query("""
    UPDATE knowledge_table
    SET state = :state, date = :date
    WHERE id = (
        SELECT id
        FROM knowledge_table
        WHERE date = '0'
        ORDER BY id
        LIMIT 1
    )
""")
    suspend fun updateFirstZeroDateKnowledge(
        date: String,
        state: String
    )


    @Query("SELECT * FROM knowledge_table WHERE state = '별' ORDER BY id DESC")
    suspend fun getStarKnowledgeData(): List<Knowledge>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<Knowledge>)
}