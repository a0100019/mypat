package com.a0100019.mypat.data.room.user

fun getUserInitialData(): List<User> {
    return listOf(
        User(id = "tutorial", value = "0"),
        User(id = "money", value = "100"),
        User(id = "cash", value = "50"),
        User(id = "name", value = "익명"),
        User(id = "englishLevel", value = "0"),

        // 더 많은 데이터를 여기에 추가...
    )
}
