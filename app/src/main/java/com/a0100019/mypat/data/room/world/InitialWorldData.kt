package com.a0100019.mypat.data.room.world

fun getWorldInitialData(): List<World> {
    return listOf(
        World(id = 1, value = "map/forest.jpg", type = "map"),
        World(value = "1", type = "pat", situation = "love"),
        World(value = "2", type = "item"),
        World(value = "2", type = "pat", situation = "love"),
    )
}
