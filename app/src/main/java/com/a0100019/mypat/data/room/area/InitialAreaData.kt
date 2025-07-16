package com.a0100019.mypat.data.room.area


fun getAreaInitialData(): List<Area> {
    return listOf(

        Area( name = "해변", date = "1", url = "area/beach.jpg"),
        Area( name = "숲", date = "1", url = "area/forest.jpg"),
        Area( name = "숲", url = "area/forest.jpg")

    )
}
