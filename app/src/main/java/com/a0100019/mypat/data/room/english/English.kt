package com.a0100019.mypat.data.room.english

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "english_table")
data class English(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val date: String = "0",
    val word: String,
    val meaning: String,
    val example: String,
    val exampleMeaning: String,
    val level: String
)
