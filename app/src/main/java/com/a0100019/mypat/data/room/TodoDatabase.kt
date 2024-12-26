package com.a0100019.mypat.data.room


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Todo::class, Note::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun noteDao(): NoteDao
}