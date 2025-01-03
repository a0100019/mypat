package com.a0100019.mypat.data.room.diary

import androidx.room.Entity
import androidx.room.PrimaryKey

//객체 형태를 만드는 코드
@Entity(tableName = "diary_table")
data class Diary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val mood: String,
    val title: String,
    val contents: String,
)

