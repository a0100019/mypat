package com.a0100019.mypat.data.room.item

fun getItemInitialData(): List<Item> {
    return listOf(
        Item( name = "책상", memo = "책상.", date = "1", url = "item/table.png", sizeFloat = 0.1f, x = 0.2f),
        Item( name = "분수", memo = "분수.", date = "1", url = "item/fountain.png", sizeFloat = 0.1f),
        Item( name = "비행기", memo = "비행기.", date = "1", url = "item/airplane.json", sizeFloat = 0.1f),
        Item( name = "비행기", memo = "비행기.", url = "item/airplane.json", sizeFloat = 0.1f),

        Item( name = "해변", memo = "해변이다.", date = "1", url = "map/beach.jpg", type = "map"),
        Item( name = "숲", memo = "숲이다.", date = "1", url = "map/forest.jpg", type = "map"),
        Item( name = "숲", memo = "숲이다.", url = "map/forest.jpg", type = "map")




    )
}
