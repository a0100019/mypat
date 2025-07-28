package com.a0100019.mypat.data.room.letter

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.a0100019.mypat.data.room.sudoku.Sudoku
import com.a0100019.mypat.data.room.world.World
import kotlinx.coroutines.flow.Flow

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