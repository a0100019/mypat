package com.a0100019.mypat.data.room.user

import androidx.room.Entity
import androidx.room.PrimaryKey

//객체 형태를 만드는 코드
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = false) val id: String,
    var value: String = "0",
    var value2: String = "0",
    val value3: String = "0",
)

