package com.a0100019.mypat.data.room.letter

import androidx.room.PrimaryKey
import com.a0100019.mypat.data.room.sudoku.Sudoku

fun getLetterInitialData(): List<Letter> {
    return listOf(
        //state - waiting, open, lead, get
        Letter(state = "waiting", title = "첫 편지", image = "sample.png@sample.png", link = "https://www.naver.com/", reward = "cash", amount = "100" ),
        Letter(state = "open", title = "2 편지", image = "sample.png@sample2.png", link = "naver.com", reward = "cash", amount = "100" ),
        Letter(state = "lead", title = "3 편지", image = "sample.png@sample2.png", link = "naver.com", reward = "cash", amount = "100" ),
        Letter(state = "get", title = "4 편지", image = "sample.png@sample2.png", link = "naver.com", reward = "cash", amount = "100" ),


        // 더 많은 데이터를 여기에 추가...
    )
}

