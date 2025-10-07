package com.a0100019.mypat.data.room.world

fun getWorldInitialData(): List<World> {
    return listOf(
        World(id = 1, value = "area/normal.webp", type = "area"),
        World(value = "1", type = "pat"),
        World(value = "21", type = "item"),
    )
}
