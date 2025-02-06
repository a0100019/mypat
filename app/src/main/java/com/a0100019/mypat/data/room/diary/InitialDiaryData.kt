@file:Suppress("UNREACHABLE_CODE")

package com.a0100019.mypat.data.room.diary

fun getDiaryInitialData() : List<Diary> {
    return listOf(
        Diary(date = "2025-02-06", mood = "happy", title = "안녕", contents = "안녕안녕안녕"),
        Diary(date = "2025-02-07", mood = "", title = "", contents = "")

    )
}