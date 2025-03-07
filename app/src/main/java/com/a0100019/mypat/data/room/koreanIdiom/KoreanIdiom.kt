package com.a0100019.mypat.data.room.koreanIdiom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "koreanIdiom_table")
data class KoreanIdiom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var date: String = "0",
    var state: String = "미정",
    val idiom: String = "한자",
    val korean: String = "해설",
    val meaning: String = "뜻",
    val korean1: String = "1",
    val korean2: String = "2",
    val korean3: String = "3",
    val korean4: String = "4",
    val image: String = "koreanIdiomImage/jukmagow1.jpg",
)
