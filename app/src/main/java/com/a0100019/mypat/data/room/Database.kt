package com.a0100019.mypat.data.room


import androidx.room.Database
import androidx.room.RoomDatabase
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.allUser.AllUserDao
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.English
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.data.room.letter.LetterDao
import com.a0100019.mypat.data.room.area.AreaDao
import com.a0100019.mypat.data.room.sudoku.Sudoku
import com.a0100019.mypat.data.room.sudoku.SudokuDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.data.room.world.WorldDao

@Database(entities = [User::class, Walk::class, Diary::class, English::class, KoreanIdiom::class, Pat::class, Item::class, World::class, Sudoku::class, Letter::class, AllUser::class, Area::class], version = 2, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun walkDao(): WalkDao
    abstract fun diaryDao(): DiaryDao
    abstract fun englishDao(): EnglishDao
    abstract fun koreanIdiomDao(): KoreanIdiomDao
    abstract fun patDao(): PatDao
    abstract fun itemDao(): ItemDao
    abstract fun worldDao(): WorldDao
    abstract fun sudokuDao(): SudokuDao
    abstract fun letterDao(): LetterDao
    abstract fun allUserDao() : AllUserDao
    abstract fun areaDao() : AreaDao

}