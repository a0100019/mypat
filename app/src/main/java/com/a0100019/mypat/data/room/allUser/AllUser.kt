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
    val like: String = "0",
    val warning: String = "0",
    val firstDate: String = "0",
    val openItem: String = "0",
    val openItemSpace: String = "0",
    val map: String = "0",
    val name: String = "0",
    val openPat: String = "0",
    val openPatSpace: String = "0",
    val totalDate: String = "0",
    val worldData0: String = "0",  // "id@size@type@x@y"
    val worldData1: String = "0",
    val worldData2: String = "0",
    val worldData3: String = "0",
    val worldData4: String = "0",
    val worldData5: String = "0",
    val worldData6: String = "0",
    val worldData7: String = "0",
    val worldData8: String = "0",
    val worldData9: String = "0",
    val worldData10: String = "0",
    val worldData11: String = "0",
    val worldData12: String = "0",
    val worldData13: String = "0",
    val worldData14: String = "0",
    val worldData15: String = "0",
    val worldData16: String = "0",
    val worldData17: String = "0",
    val worldData18: String = "0",
    val worldData19: String = "0",


)

