package com.a0100019.mypat.data.room.world

import com.a0100019.mypat.data.room.pet.Pat

fun getWorldInitialData(): List<World> {
    return listOf(
        World(id = "map", value = "map/forest.jpg", type = "map"),
        World(id = "pat1", value = "1", open = "1", type = "pat"),
        World(id = "pat2", value = "2", open = "1", type = "pat"),
        World(id = "pat3", type = "pat"),
        World(id = "pat4", type = "pat"),
        World(id = "pat5", type = "pat"),
        World(id = "item1", value = "1", open = "1", type = "item"),
        World(id = "item2", value = "2", open = "1", type = "item"),
        World(id = "item3", type = "item"),
        World(id = "item4", type = "item"),
        World(id = "item5", type = "item")
    )
}
