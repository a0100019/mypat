@file:Suppress("UNREACHABLE_CODE")

package com.a0100019.mypat.data.room.diary

fun getDiaryInitialData() : List<Diary> {
    return listOf(
        Diary(date = "2025-02-06", contents = "안녕안녕안녕", state = "완료"),
        Diary(date = "2025-03-07", contents = "")

    )
}