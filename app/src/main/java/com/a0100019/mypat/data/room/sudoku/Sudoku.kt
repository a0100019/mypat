package com.a0100019.mypat.data.room.sudoku

import androidx.room.Entity
import androidx.room.PrimaryKey

//객체 형태를 만드는 코드
@Entity(tableName = "sudoku_table")
data class Sudoku(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var date: String = "0",
    val name: String = "",
    val url: String,
    var love: Int = 0,
    var x: Float = 0.5f,
    var y: Float = 0.5f,
    val touch: Long? = null,
    val minFloat: Float = 0.1f,
    val sizeFloat: Float = 0.2f,
    val category: String = "1",
    val memo: String = "",

    )
