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

    fun onNumberClick(number: Int) = intent {

    }

    fun onPuzzleClick(rowIndex : Int, colIndex : Int) = intent {
        reduce {
            state.copy(
                clickedPuzzle = rowIndex.toString() + colIndex.toString()
            )
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
        // 2차원 배열을 리스트로 변환해서 상태 업데이트
        val newBoard = board.map { it.toList() }
        reduce { state.copy(sudokuBoard = newBoard) }
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
    val clickedPuzzle : String = "99",
    val time : Double = 0.0
)


//상태와 관련없는 것
sealed interface ThirdGameSideEffect{
    class Toast(val message:String): ThirdGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}
