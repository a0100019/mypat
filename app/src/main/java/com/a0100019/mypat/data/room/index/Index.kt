package com.a0100019.mypat.data.room.index

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "index_table")
data class Index(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val date: String = "0",
    val name: String,
    val memo: String,
    val url: String,
    val love: Int = 0,
    val out: String = "0",
    val x: Int = 0,
    val y: Int = 0,
)
