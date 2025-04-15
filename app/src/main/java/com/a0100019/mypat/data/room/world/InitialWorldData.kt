package com.a0100019.mypat.data.room.world

import com.a0100019.mypat.data.room.pet.Pat

fun getWorldInitialData(): List<World> {
    return listOf(
        World(id = "map", value = "map/forest.jpg", type = "map"),
        World(id = "1", value = "1", open = "1", type = "pat"),
        World(id = "2", value = "2", open = "1", type = "pat"),
        World(id = "3", open = "1", type = "pat"),
        World(id = "4", type = "pat"),
        World(id = "5", type = "pat"),
        World(id = "6", value = "0", open = "1", type = "item"),
        World(id = "7", value = "0", open = "1", type = "item"),
        World(id = "8", open = "1", type = "item"),
        World(id = "9", type = "item"),
        World(id = "10", type = "item")
    )
}
