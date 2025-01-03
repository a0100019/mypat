package com.a0100019.mypat.data.room.world

import com.a0100019.mypat.data.room.pet.Pat

fun getWorldInitialData(): List<World> {
    return listOf(
        World(id = "map", value = "map/beach.jpg"),
        World(id = "pat1", value = "1", open = "1"),
        World(id = "pat2"),
        World(id = "pat3"),
        World(id = "pat4"),
        World(id = "pat5"),
        World(id = "item1", value = "1", open = "1"),
        World(id = "item2"),
        World(id = "item3"),
        World(id = "item4"),
        World(id = "item5")
    )
}
