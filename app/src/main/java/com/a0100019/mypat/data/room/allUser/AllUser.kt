package com.a0100019.mypat.data.room.allUser

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.a0100019.mypat.data.room.user.User

//객체 형태를 만드는 코드
@Entity(tableName = "allUser_table")
data class AllUser(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tag: String = "0",
    val lastLogIn: Long = 0,
    val ban: String = "0",
    var like: String = "0",
    val warning: String = "0",
    val firstDate: String = "0",
    val firstGame: String = "0",
    val secondGame: String = "0",
    val thirdGameEasy: String = "0",
    val thirdGameNormal: String = "0",
    val thirdGameHard: String = "0",
    val openItem: String = "0",
    val map: String = "0",
    val name: String = "0",
    val openPat: String = "0",
    val openMap: String = "0",
    val totalDate: String = "0",
    val worldData: String = "0",  // "id@size@type@x@y/id@size@type@x@y"

)

