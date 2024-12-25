package com.a0100019.mypat.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.a0100019.mypat.data.room.NoteDao
import com.a0100019.mypat.data.room.TodoDao
import com.a0100019.mypat.data.room.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_database"
        ).addMigrations(MIGRATION_1_2)
            .build()
    }

    //테이블 추가 같은 데이터베이스 변경 사항은 아래의 마이그레이션 코드가 있어야 버전 업데이트가 진행됨 아니면 이전 데이터를 못가져옴
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 새 테이블 생성 쿼리
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS `note_table` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `title` TEXT NOT NULL, 
                `content` TEXT NOT NULL, 
                `createdAt` INTEGER NOT NULL
            )
        """)
        }
    }


    @Provides
    fun provideTodoDao(database: TodoDatabase): TodoDao {
        return database.todoDao()
    }

    @Provides
    fun provideNoteDao(database: TodoDatabase): NoteDao {
        return database.noteDao()  // NoteDao 추가
    }
}
