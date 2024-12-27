package com.a0100019.mypat.data.room


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, Walk::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun walkDao(): WalkDao
}