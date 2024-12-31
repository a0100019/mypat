package com.a0100019.mypat.data.room.item

fun getItemInitialData(): List<Item> {
    return listOf(
        Item( name = "책상", memo = "책상.", url = "item/table.png"),

        Item( name = "해변", memo = "해변이다.", url = "map/beach.jpg", category = "map")


    )
}
