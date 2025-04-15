package com.a0100019.mypat.data.room.world

import com.a0100019.mypat.data.room.pet.Pat

fun getWorldInitialData(): List<World> {
    return listOf(
        World(id = "map", value = "map/forest.jpg", type = "map"),
        World(id = "1", value = "1", open = "1", type = "pat"),
        World(id = "2", value = "2", open = "1", type = "item"),
        World(id = "3", value = "2", open = "1", type = "pat"),
        World(id = "4", ),
        World(id = "5", ),
        World(id = "6", ),
        World(id = "7", ),
        World(id = "8", ),
        World(id = "9", ),
        World(id = "10", )
    )
}
