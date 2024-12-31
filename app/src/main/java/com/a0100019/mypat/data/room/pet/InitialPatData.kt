package com.a0100019.mypat.data.room.pet

fun getPatInitialData(): List<Pat> {
    return listOf(
        Pat(name = "고양이", memo = "귀여운 고양이", url = "pat/cat.json")
    )
}
