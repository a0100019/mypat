package com.a0100019.mypat.data.room.area


fun getAreaInitialData(): List<Area> {
    return listOf(

        Area( name = "해변", memo = "해변이다.", date = "1", url = "area/beach.jpg"),
        Area( name = "숲", memo = "숲이다.", date = "1", url = "area/forest.jpg"),
        Area( name = "숲", memo = "숲이다.", url = "area/forest.jpg")

    )
}
