@file:Suppress("UNREACHABLE_CODE")

package com.a0100019.mypat.data.room.english

import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom

fun getEnglishInitialData(): List<English> {
    return listOf(
        English(
            word = "apple",
            state = "대기",
            meaning = "사과",
        ),
        English(
            word = "mango",
            meaning = "망고",
            state = "대기"
        ),
        English(
            word = "panda",
            meaning = "판다",
        ),
        English(
            word = "study",
            meaning = "공부",
            state = "완료"
        ),

    )
}
