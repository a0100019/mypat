package com.a0100019.mypat.data.room.allUser

import androidx.room.Entity
import androidx.room.PrimaryKey

//객체 형태를 만드는 코드
@Entity(tableName = "allUser_table")
data class AllUser(
    @PrimaryKey val tag: String = "0",
    val lastLogin: Long = 0,
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
    val area: String = "0",
    val name: String = "0",
    val openPat: String = "0",
    val openArea: String = "0",
    val totalDate: String = "0",
    val worldData: String = "0",  // "id@size@type@x@y/id@size@type@x@y"

)

