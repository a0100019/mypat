package com.a0100019.mypat.data.room.pet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pat_table")
data class Pat(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val date: String = "0",
    val name: String,
    val memo: String,
    val url: String,
    val love: Int = 0,
    val category: String = "1"

)
