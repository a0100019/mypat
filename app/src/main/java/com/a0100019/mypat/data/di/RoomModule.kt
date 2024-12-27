package com.a0100019.mypat.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.a0100019.mypat.data.room.WalkDao
import com.a0100019.mypat.data.room.UserDao
import com.a0100019.mypat.data.room.Database
import com.a0100019.mypat.data.room.getUserInitialData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // 데이터베이스가 처음 생성될 때 초기 데이터 삽입
                    CoroutineScope(Dispatchers.IO).launch {

                        val userDao = provideDatabase(context).userDao()
                        val userInitialData = getUserInitialData()
                        userDao.insertAll(userInitialData) // 대량 삽입

                    }
                }
            })
            .build()
    }

//    //테이블 추가 같은 데이터베이스 변경 사항은 아래의 마이그레이션 코드가 있어야 버전 업데이트가 진행됨 아니면 이전 데이터를 못가져옴
//    private val MIGRATION_1_2 = object : Migration(1, 2) {
//        override fun migrate(db: SupportSQLiteDatabase) {
//            // 새 테이블 생성 쿼리
//            db.execSQL("""
//            CREATE TABLE IF NOT EXISTS `note_table` (
//                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
//                `title` TEXT NOT NULL,
//                `content` TEXT NOT NULL,
//                `createdAt` INTEGER NOT NULL
//            )
//        """)
//        }
//    }


    @Provides
    fun provideUserDao(database: Database): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideNoteDao(database: Database): WalkDao {
        return database.walkDao()  // NoteDao 추가
    }
}
