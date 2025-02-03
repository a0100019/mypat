package com.a0100019.mypat.data.room.item

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_table")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String = "0",
    val name: String = "",
    val memo: String = "",
    val url: String,
    var x: Float = 0.5f,
    var y: Float = 0.5f,
    val touch: Long? = null,
    val minFloat: Float = 0.1f,
    val sizeFloat: Float = 0.2f,
    val category: String = "1",
    val type: String = "item"

    )
