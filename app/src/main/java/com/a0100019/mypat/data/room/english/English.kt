package com.a0100019.mypat.data.room.english

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "english_table")
data class English(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var date: String = "0",
    var state: String = "미정",
    val word: String = "단어",
    val meaning: String = "뜻",
)
