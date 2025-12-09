package com.a0100019.mypat.data.room.letter

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LetterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(letter: Letter)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(letter: Letter)

    @Delete
    suspend fun delete(letter: Letter)

    @Query("DELETE FROM letter_table")
    suspend fun deleteAllLetters()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'letter_table'")
    suspend fun resetLetterPrimaryKey()

    @Update
    suspend fun update(letter: Letter)

    // ✅ title 기준으로 state가 "waiting"인 경우만 state를 "open"으로 변경하고 date를 오늘 날짜로 업데이트
    @Query("""
    UPDATE letter_table
    SET state = 'open', date = :todayDate
    WHERE title = :title AND state = 'waiting'
""")
    suspend fun updateDateByTitle(title: String, todayDate: String)

    // ✅ title을 새 값으로 변경하고, state가 "waiting"일 때만 "open"으로 변경
    @Query("""
    UPDATE letter_table
    SET title = :newTitle,
        state = 'open',
        date = :todayDate
    WHERE title = :oldTitle AND state = 'waiting'
""")
    suspend fun updateTitleAndOpenState(oldTitle: String, newTitle: String, todayDate: String): Int

    @Query("""
    SELECT *
    FROM letter_table
    WHERE state != 'waiting'
    ORDER BY id DESC
""")
    suspend fun getNotWaitingLetterData(): List<Letter>

    @Query("""
        SELECT *
        FROM letter_table
        ORDER BY id DESC
        """)
    suspend fun getAllLetterData(): List<Letter>

    //초기에 데이터 한번에 넣기 위한 코드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(letter: List<Letter>)
}