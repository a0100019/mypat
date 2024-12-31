package com.a0100019.mypat.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.Database
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.english.getEnglishInitialData
import com.a0100019.mypat.data.room.index.IndexDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.koreanIdiom.getKoreanIdiomInitialData
import com.a0100019.mypat.data.room.user.getUserInitialData
import com.a0100019.mypat.data.room.world.WorldDao
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

                        //data 삽입
                        val userDao = provideDatabase(context).userDao()
                        val userInitialData = getUserInitialData()
                        userDao.insertAll(userInitialData) // 대량 삽입

                        val englishDao = provideDatabase(context).englishDao()
                        val englishInitialData = getEnglishInitialData()
                        englishDao.insertAll(englishInitialData) // 대량 삽입

                        val koreanIdiomDao = provideDatabase(context).koreanIdiomDao()
                        val koreanIdiomInitialData = getKoreanIdiomInitialData()
                        koreanIdiomDao.insertAll(koreanIdiomInitialData) // 대량 삽입

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
    fun provideWalkDao(database: Database): WalkDao {
        return database.walkDao()
    }

    @Provides
    fun provideDiaryDao(database: Database): DiaryDao {
        return database.diaryDao()
    }

    @Provides
    fun provideEnglishDao(database: Database): EnglishDao {
        return database.englishDao()
    }

    @Provides
    fun provideKoreanIdiomDao(database: Database): KoreanIdiomDao {
        return database.koreanIdiomDao()
    }

    @Provides
    fun provideIndexDao(database: Database): IndexDao {
        return database.indexDao()
    }

    @Provides
    fun provideWorldDao(database: Database): WorldDao {
        return database.worldDao()
    }
}


