package com.a0100019.mypat.data.room.sudoku

import com.a0100019.mypat.data.room.user.User

fun getSudokuInitialData(): List<Sudoku> {
    return listOf(
        Sudoku(id = "sudokuBoard"),
        Sudoku(id = "sudokuFirstBoard"),
        Sudoku(id = "sudokuMemoBoard"),
        Sudoku(id = "time"),
        Sudoku(id = "level", value = "0"),
        Sudoku(id = "state", value = "0")

        // 더 많은 데이터를 여기에 추가...
    )
}

