package com.a0100019.mypat.data.room


import androidx.room.Database
import androidx.room.RoomDatabase
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.English
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.index.Index
import com.a0100019.mypat.data.room.index.IndexDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao

@Database(entities = [User::class, Walk::class, Diary::class, English::class, KoreanIdiom::class, Index::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun walkDao(): WalkDao
    abstract fun diaryDao(): DiaryDao
    abstract fun englishDao(): EnglishDao
    abstract fun koreanIdiomDao(): KoreanIdiomDao
    abstract fun indexDao(): IndexDao
}