package com.a0100019.mypat.data.room.knowledge

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_table")
data class Knowledge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var date: String = "0",
    var state: String = "미정",
    val answer: String = "답",
    val meaning: String = "뜻",
)
