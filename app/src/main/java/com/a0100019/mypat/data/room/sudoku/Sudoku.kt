package com.a0100019.mypat.data.room.sudoku

import androidx.room.Entity
import androidx.room.PrimaryKey

//객체 형태를 만드는 코드
@Entity(tableName = "sudoku_table")
data class Sudoku(

    //autoGenerate = true 는 Int만 가능
    @PrimaryKey(autoGenerate = false) val id: String,
    var value: String = "0",
    var value2: String = "0",
    var value3: String = "0",

    )
