package com.a0100019.mypat.presentation.game.thirdGame

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class ThirdGameViewModel @Inject constructor(
    private val userDao: UserDao,
) : ViewModel(), ContainerHost<ThirdGameState, ThirdGameSideEffect> {

    override val container: Container<ThirdGameState, ThirdGameSideEffect> = container(
        initialState = ThirdGameState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(ThirdGameSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadData()
    }

    //room에서 데이터 가져옴
    private fun loadData() = intent {

    }

    fun dataSave() = intent {
        val sudokuBoard = state.sudokuBoard
        val sudokuBoardString = buildString {
            for (rowIndex in sudokuBoard.indices) {
                for (colIndex in sudokuBoard[rowIndex].indices) {
                    val value = sudokuBoard[rowIndex][colIndex]
                    if (value != 0) {
                        // 값이 0이 아닌 경우 "rowIndex-colIndex-value" 형태로 추가
                        append("$rowIndex-$colIndex-$value.")
                    }
                }
            }
            // 마지막 마침표(.) 제거
            if (isNotEmpty()) deleteAt(lastIndex)
        }
    }

    fun onEraserClick() = intent {
        if(state.clickedPuzzle != "99") {
            val row = state.clickedPuzzle[0].digitToInt()
            val col = state.clickedPuzzle[1].digitToInt()

            val newSudoku = state.sudokuBoard.map { it.toMutableList() }.toMutableList()
            newSudoku[row][col] = 0

            reduce {
                state.copy(
                    sudokuBoard = newSudoku
                )
            }
        }
    }

    fun onMemoClick() = intent {

        reduce {
            state.copy(
                memoMode = !state.memoMode
            )
        }
    }

    fun onMemoNumberClick(number: Int) = intent {

        if(state.clickedPuzzle != "99") {
            val row = state.clickedPuzzle[0].digitToInt()
            val col = state.clickedPuzzle[1].digitToInt()

            val newSudoku = state.sudokuMemoBoard.map { it.toMutableList() }.toMutableList()

            val currentValue = newSudoku[row][col]

            val newValue = if (currentValue.contains(number.toString())) {
                currentValue.replace(number.toString(), "") // 숫자가 있으면 제거
            } else {
                (currentValue + number.toString()).toCharArray().sorted()
                    .joinToString("") // 숫자가 없으면 추가 후 정렬
            }

            newSudoku[row][col] = newValue

            reduce {
                state.copy(
                    sudokuMemoBoard = newSudoku
                )
            }
        }


    }

    fun onNumberClick(number: Int) = intent {

        if(state.clickedPuzzle != "99") {
            val row = state.clickedPuzzle[0].digitToInt()
            val col = state.clickedPuzzle[1].digitToInt()

            val newSudoku = state.sudokuBoard.map { it.toMutableList() }.toMutableList()
            newSudoku[row][col] = number

            reduce {
                state.copy(
                    sudokuBoard = newSudoku
                )
            }

            if (newSudoku.all { row -> row.all { it != 0 } }) {
                var success = 0
                // 0이 없으면 실행할 코드
                repeat(9) { it ->
                    if (newSudoku[it].sum() != 45) {
                        success++
                    }
                }

                repeat(9) { it ->
                    if (newSudoku.sumOf { it[col] } != 45) {
                        success++
                    }
                }

                if (success == 0) {
                    //성공
                    reduce {
                        state.copy(
                            gameState = "성공"
                        )
                    }
                } else {
                    postSideEffect(ThirdGameSideEffect.Toast("오류가 있습니다."))
                }
            }
        }


    }

    fun onPuzzleClick(rowIndex : Int, colIndex : Int) = intent {
        if(state.clickedPuzzle == rowIndex.toString() + colIndex.toString()) {
            reduce {
                state.copy(
                    clickedPuzzle = "99"
                )
            }
        } else {
            reduce {
                state.copy(
                    clickedPuzzle = rowIndex.toString() + colIndex.toString()
                )
            }
        }

    }

    fun makeSudoku() = intent {
        val board = Array(9) { IntArray(9) { 0 } }

        fun isValid(row: Int, col: Int, num: Int): Boolean {
            for (i in 0 until 9) {
                if (board[row][i] == num || board[i][col] == num) return false
            }
            val boxRowStart = (row / 3) * 3
            val boxColStart = (col / 3) * 3
            for (i in 0 until 3) {
                for (j in 0 until 3) {
                    if (board[boxRowStart + i][boxColStart + j] == num) return false
                }
            }
            return true
        }

        fun fillBoard(row: Int = 0, col: Int = 0): Boolean {
            if (row == 9) return true
            val nextRow = if (col == 8) row + 1 else row
            val nextCol = if (col == 8) 0 else col + 1

            if (board[row][col] != 0) return fillBoard(nextRow, nextCol)

            val numbers = (1..9).shuffled()
            for (num in numbers) {
                if (isValid(row, col, num)) {
                    board[row][col] = num
                    if (fillBoard(nextRow, nextCol)) return true
                    board[row][col] = 0
                }
            }
            return false
        }

        fillBoard()

        val positions = mutableListOf<Pair<Int, Int>>()

        // 모든 위치를 리스트에 추가
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                positions.add(i to j)
            }
        }

        // 무작위로 섞고 처음 count 개만 선택하여 0으로 변경
        positions.shuffled().take(3).forEach { (row, col) ->
            board[row][col] = 0
        }
        // 2차원 배열을 리스트로 변환해서 상태 업데이트
        val newBoard = board.map { it.toList() }
        reduce { state.copy(
            sudokuBoard = newBoard,
            sudokuFirstBoard = newBoard
        ) }
    }

    private fun stopTimer() {
        //타이머 종료
        timerJob?.cancel()
    }

    // 타이머 시작
    private var timerJob: Job? = null
    private fun startTimer() {
        timerJob?.cancel() // 기존 타이머 중지
        timerJob = viewModelScope.launch {
            val startTime = SystemClock.elapsedRealtime() // 시작 시간 기록
            while (true) {
                val elapsed = (SystemClock.elapsedRealtime() - startTime) / 1000.0 // 경과 시간(초)
                intent {
                    reduce { state.copy(time = elapsed) } // 정확한 시간 반영
                }
                delay(10L) // 10ms마다 체크 (하지만 실제 시간은 SystemClock 기반)
            }
        }
    }


}


@Immutable
data class ThirdGameState(
    val userData: List<User> = emptyList(),
    val sudokuBoard: List<List<Int>> = List(9) { List(9) { 0 } }, // 9x9 스도쿠 보드 추가
    val sudokuFirstBoard: List<List<Int>> = List(9) { List(9) { 0 } }, // 9x9 스도쿠 보드 추가
    val sudokuMemoBoard: List<List<String>> = List(9) { List(9) { "" } }, // 9x9 메모 스도쿠 보드 추가
    val clickedPuzzle : String = "99",
    val time : Double = 0.0,
    val gameState : String = "대기",
    val memoMode : Boolean = false,
)


//상태와 관련없는 것
sealed interface ThirdGameSideEffect{
    class Toast(val message:String): ThirdGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}
