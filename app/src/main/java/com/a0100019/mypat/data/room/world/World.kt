package com.a0100019.mypat.data.room.world

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_table")
data class World(
    @PrimaryKey(autoGenerate = false) val id: String,
    val value: String = "0",
    val open: String = "0",
    val type: String = ""
//    touch = System.currentTimeMillis() // 현재 시간 저장
    )
