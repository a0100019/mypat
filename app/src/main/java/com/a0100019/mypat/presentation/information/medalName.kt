package com.a0100019.mypat.presentation.information

fun medalName(type: Int): String =
    when (type) {
        0 -> "없음"
        1 -> "신입 관리인"
        //칭호 설명
        2 -> "동물 사랑꾼"
        3 -> "동물 레벨 100"
        4 -> "펫 게임 100회"
        5 -> "사랑일기 10개"
        6 -> "한자 별 10개"
        7 -> "영어푼거 50개"
        8 -> "연속출석10일"
        9 -> "서울부산 걷기"
        10 -> "아이템 다 모으고 누르기"
        11 -> "좋아요 100개"
        12 -> "게시글 3개"
        13 -> "게시글 댓글 10개"
        14 -> "채팅 20개"
        15 -> "명상 나무늘보 도감 페이지 10번 들어가기"
        16 -> "애국자 대한민국 깃발 10번 들어가기"
        17 -> "대나무숲 1회 작성"
        18 -> "스토리 3번 읽기"
        19 -> "꼼꼬미"
        20 -> "개인 채팅 수 10개"
        21 -> "친구와 채팅 100개 이상"
        22 -> "꾸준한 관리인"
        else -> "알 수 없음"
    }

//20 -> "하루마을 후원자"
//21 -> "하루마을 공헌자"

fun medalExplain(type: Int): String =
    when (type) {
        0 -> "없음"
        1 -> "하루마을 출석"
        //칭호 설명
        2 -> "동물 레벨 50"
        3 -> "동물 레벨 100"
        4 -> "펫 게임 100회"
        5 -> "뾰로롱"
        6 -> "뾰로롱"
        7 -> "뾰로롱"
        8 -> "뾰로롱"
        9 -> "뾰로롱"
        10 -> "뾰로롱"
        11 -> "뾰로롱"
        12 -> "뾰로롱"
        13 -> "뾰로롱"
        14 -> "뾰로롱"
        15 -> "뾰로롱"
        16 -> "뾰로롱"
        17 -> "대나무숲 1회 작성"
        18 -> "스토리 3번 읽기"
        19 -> "꼼꼬미"
        20 -> "개인 채팅 수 10개"
        21 -> "친구와 채팅 100개 이상"
        22 -> "꾸준한 관리인"
        else -> "알 수 없음"
    }

fun addMedalAction(medalData: String, actionId: Int): String {
    // 기본값 처리
    if (medalData == "0") {
        return "$actionId/1"
    }

    val map = medalData
        .split("@")
        .mapNotNull {
            val parts = it.split("/")
            if (parts.size == 2) {
                parts[0].toIntOrNull()?.let { id ->
                    id to (parts[1].toIntOrNull() ?: 0)
                }
            } else null
        }
        .toMap()
        .toMutableMap()

    // 카운트 증가
    map[actionId] = (map[actionId] ?: 0) + 1

    // 다시 String으로 변환
    return map.entries.joinToString("@") { "${it.key}/${it.value}" }
}

fun getMedalActionCount(medalData: String, actionId: Int): Int {
    if (medalData == "0" || medalData.isBlank()) return 0

    return medalData
        .split("@")
        .firstOrNull {
            it.startsWith("$actionId/")
        }
        ?.substringAfter("/")
        ?.toIntOrNull()
        ?: 0
}

fun totalMedalCount(): Int {
    return (1..500) // 충분히 큰 범위
        .count { medalName(it) != "알 수 없음" }
}
