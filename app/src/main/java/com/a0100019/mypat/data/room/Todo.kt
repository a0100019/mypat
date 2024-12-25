package com.a0100019.mypat.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

//객체 형태를 만드는 코드
@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isDone: Boolean = false
)

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,        // 메모의 제목
    val content: String,      // 메모의 내용
    val createdAt: Long = System.currentTimeMillis() // 메모 작성 시간 (타임스탬프)
)
