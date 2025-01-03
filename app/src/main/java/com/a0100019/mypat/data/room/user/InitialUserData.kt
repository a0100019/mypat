package com.a0100019.mypat.data.room.user

fun getUserInitialData(): List<User> {
    return listOf(
        //value = 최대 사용 개수, value2 = 오픈된 사용 개수, value3 = 사용한 수
        User(id = "pat", value = "5", value2 = "2", value3 = "0"),
        User(id = "item", value = "5", value2 = "2", value3 = "0"),
        User(id = "tutorial", value = "0"),
        User(id = "money", value = "100"),
        User(id = "cash", value = "50"),
        User(id = "name", value = "익명"),
        User(id = "englishLevel", value = "0"),

        // 더 많은 데이터를 여기에 추가...
    )
}
