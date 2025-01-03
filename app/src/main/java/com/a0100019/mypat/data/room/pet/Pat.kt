package com.a0100019.mypat.data.room.pet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pat_table")
data class Pat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String = "0",
    val name: String = "",
    val memo: String = "",
    val url: String,
    val love: Int = 0,
    val x: Float = 0.5f,
    val y: Float = 0.5f,
    val touch: Long? = null,
    val sizeFloat: Float = 0.2f,
    val category: String = "1"

)
