package com.a0100019.mypat.data.room.letter

import androidx.room.PrimaryKey
import com.a0100019.mypat.data.room.sudoku.Sudoku

fun getLetterInitialData(): List<Letter> {
    return listOf(
        //state - waiting, open, read
        Letter(state = "waiting", title = "첫 편지", message = "안녕하세요 저는 이유빈입니다.\n안녕하세요 저는 이유빈입니다.\n안녕하세요 저는 이유빈입니다.\n", link = "https://www.naver.com/", reward = "cash", amount = "100" ),
        Letter(state = "open", date = "2025-01-01", message = "안녕하세요 저는 이유빈입니다.안녕하세요 저는 이유빈입니다.안녕하세요 저는 이유빈입니다.", title = "2 편지", link = "naver.com", reward = "cash", amount = "100" ),
        Letter(state = "read", date = "2025-01-01", title = "3 편지", message = "aa", link = "https://www.naver.com/", reward = "cash", amount = "100" ),

        // 더 많은 데이터를 여기에 추가...
    )
}