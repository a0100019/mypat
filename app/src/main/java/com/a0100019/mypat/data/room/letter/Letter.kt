package com.a0100019.mypat.data.room.letter

import androidx.room.Entity
import androidx.room.PrimaryKey

//객체 형태를 만드는 코드
@Entity(tableName = "letter_table")
data class Letter(

    //autoGenerate = true 는 Int만 가능
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String = "0",
    val title: String = "0",
    val image: String = "0",
    val link: String = "0",
    val reward: String = "0",
    val amount: String = "0",
    var state: String = "open"

    )
