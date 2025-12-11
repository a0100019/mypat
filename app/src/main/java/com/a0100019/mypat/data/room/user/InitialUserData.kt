package com.a0100019.mypat.data.room.user

fun getUserInitialData(): List<User> {
    return listOf(
        //value = 최대 사용 개수, value2 = 오픈된 사용 개수, value3 = 사용한 수
        User(id = "date", value = "2001-01-01", value2 = "0", value3 = "2001-01-01"), // 오늘 날짜, 접속일, 첫 접속 날짜
        User(id = "selectPat"),
        User(id = "money", value = "0", value2 = "500"),
        User(id = "pat", value = "5", value2 = "1", value3 = "1"), // 1-최종 개수, 2-오픈 개수, 3-사용 개수
        User(id = "item", value = "10", value2 = "1", value3 = "1"),
        User(id = "name", value = "이웃"),
        User(id = "auth", value3 = "0"), //uid, 테그, timeStemp
        User(id = "etc"), // page, 커뮤니티업데이트날짜, 음악 켜기끄기
        User(id = "etc2"), // clickDiaryDate, stepsRaw, clickChatData
        User(id = "firstGame"), //
        User(id = "secondGame", value = "100000"), //
        User(id = "thirdGame"), //1은 쉬움, 2는 보통, 3은 어려움 개수
        User(id = "community"), // 좋아요, 경고 (신고 남발하는 사람 1로 해두면 됨), 벤 (벤 관리는 수동으로, 벤 1로 해놓으면 채팅 전송해도 사람들한테 안보이고 월드도 안보임)
        User(id = "walk") // 저장된 걸음 수, 시스템 걸음 수, 총 걸음 수

        // 더 많은 데이터를 여기에 추가...
    )
}
