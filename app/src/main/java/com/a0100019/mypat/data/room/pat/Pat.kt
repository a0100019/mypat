package com.a0100019.mypat.data.room.pat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pat_table")
data class Pat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var date: String = "0",
    val name: String = "",
    val url: String,
    var effect: Int = 0,
    var love: Int = 0,
    var x: Float = 0.5f,
    var y: Float = 0.5f,
    val minFloat: Float = 0.1f,
    var sizeFloat: Float = 0.2f,
    var gameCount: Int = 0,
    val memo: String = "",
    )
