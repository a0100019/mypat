package com.a0100019.mypat.data.room.user

fun getUserInitialData(): List<User> {
    return listOf(
        //value = 최대 사용 개수, value2 = 오픈된 사용 개수, value3 = 사용한 수
        User(id = "date", value = "0", value2 = "2025-02-05"),
        User(id = "selectPat"),
        User(id = "money", value = "1000", value2 = "1000"),
        User(id = "pat", value = "5", value2 = "3", value3 = "0"),
        User(id = "item", value = "5", value2 = "3", value3 = "0"),
        User(id = "name", value = "익명"),
        User(id = "englishLevel", value = "0"),
        User(id = "firstGame", value = "0"), //2는 level, 3은 pat index (걍 하지말자)
        User(id = "secondGame", value = "1000"), //2는 없음, 3은 pat index
        User(id = "thirdGame", value = "0"), //1은 쉬움, 2는 보통, 3은 어려움 개수

        // 더 많은 데이터를 여기에 추가...
    )
}
