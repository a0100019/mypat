package com.a0100019.mypat.data.room.walk

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "walk_table")
data class Walk(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
)
