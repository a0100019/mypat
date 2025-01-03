package com.a0100019.mypat.data.room.koreanIdiom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "koreanIdiom_table")
data class KoreanIdiom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String = "0",
    val idiom: String,
    val korean: String,
    val meaning: String,
    val korean1: String,
    val korean2: String,
    val korean3: String,
    val korean4: String,
    val image: String,
)
