package com.a0100019.mypat.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
//    @Insert
//    suspend fun insert(note: Note)
//
//    @Delete
//    suspend fun delete(note: Note)
//
//    @Query("SELECT * FROM note_table ORDER BY createdAt DESC")
//    fun getAllNotes(): Flow<List<Note>>
}