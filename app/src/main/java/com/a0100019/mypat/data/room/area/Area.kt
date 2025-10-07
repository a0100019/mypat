package com.a0100019.mypat.data.room.area

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "area_table")
data class Area(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var date: String = "0",
    val name: String = "",
    val url: String = "area/normal.webp",

    )
