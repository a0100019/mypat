package com.a0100019.mypat.presentation.game.thirdGame

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.sudoku.SudokuDao
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
    private val patDao: PatDao,
    private val sudokuDao: SudokuDao,
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

    private fun loadData() = intent {
        val userDataList = userDao.getAllUserData()
        val patData = patDao.getPatDataById(userDataList.find { it.id == "selectPat" }?.value ?: "0")

        reduce {
            state.copy(
                userData = userDataList,
                patData = patData
            )
        }

        val sudokuDataList = sudokuDao.getAllSudokuData()
        if(sudokuDataList.find { it.id == "state" }!!.value == "0") {
            reduce {
                state.copy(
                    gameState = "설정"
                )
            }
        } else {

            //이어하기
            val sudokuBoard = returnSudokuBoard(sudokuDataList.find { it.id == "sudokuBoard" }!!.value)
            val sudokuFirstBoard = returnSudokuBoard(sudokuDataList.find { it.id == "sudokuFirstBoard" }!!.value)
            val sudokuMemoBoard = returnSudokuBoard(sudokuDataList.find { it.id == "sudokuMemoBoard" }!!.value)
            val time = sudokuDataList.find { it.id == "time" }!!.value.toDouble()
            val level = sudokuDataList.find { it.id == "level" }!!.value

            reduce {
                state.copy(
                    sudokuBoard = sudokuBoard,
                    sudokuFirstBoard = sudokuFirstBoard,
                    sudokuMemoBoard = sudokuMemoBoard,
                    time = time,
                    level = level.toInt()
                )
            }

            startTimer()

        }

    }

    fun newGame() = intent {

        loadData()

        sudokuDao.update(id = "state", value = "0")
        sudokuDao.update(id = "time", value = "0.0")
        reduce {
            state.copy(
                gameState = "설정",
                sudokuBoard = List(9) { List(9) { "0" } },
                sudokuFirstBoard = List(9) { List(9) { "0" } },
                sudokuMemoBoard = List(9) { List(9) { "0" } },
                time = 0.0,
                clickedPuzzle = "99"
            )
        }
        stopTimer()

    }

    fun onStateChangeClick(newState: String) = intent {

        reduce {
            state.copy(
                gameState = newState
            )
        }

    }

    private fun saveData() = intent {
        val sudokuBoardString = buildStringSudokuBoard(state.sudokuBoard)
        val sudokuFirstBoardString = buildStringSudokuBoard(state.sudokuFirstBoard)
        val sudokuMemoBoardString = buildStringSudokuBoard(state.sudokuMemoBoard)
        val time = state.time
        val level = state.level

        sudokuDao.update(id = "sudokuBoard", value = sudokuBoardString)
        sudokuDao.update(id = "sudokuFirstBoard", value = sudokuFirstBoardString)
        sudokuDao.update(id = "sudokuMemoBoard", value = sudokuMemoBoardString)
        sudokuDao.update(id = "time", value = time.toString())
        sudokuDao.update(id = "level", value = level.toString())

    }

    private fun buildStringSudokuBoard(list: List<List<String>>) : String {

        val sudokuBoardString = buildString {
            for (rowIndex in list.indices) {
                for (colIndex in list[rowIndex].indices) {
                    val value = list[rowIndex][colIndex]
                    append("$rowIndex-$colIndex-$value.")
                }
            }
            // 마지막 마침표(.) 제거
            if (isNotEmpty()) deleteAt(lastIndex)
        }

        return sudokuBoardString
    }

    private fun returnSudokuBoard(data: String): List<List<String>> {
        val board = MutableList(9) { MutableList(9) { "" } } // 9x9 보드 초기화

        if (data.isNotEmpty()) {
            val cells = data.split(".")
            for (cell in cells) {
                val parts = cell.split("-")
                if (parts.size == 3) {
                    val row = parts[0].toInt()
                    val col = parts[1].toInt()
                    val value = parts[2]
                    board[row][col] = value
                }
            }
        }

        return board
    }

    fun onMemoClick() = intent {

        reduce {
            state.copy(
                memoMode = !state.memoMode
            )
        }
    }

    fun onEraserClick() = intent {

        val row = state.clickedPuzzle[0].digitToInt()
        val col = state.clickedPuzzle[1].digitToInt()

        val newSudoku = state.sudokuBoard.map { it.toMutableList() }.toMutableList()
        newSudoku[row][col] = "0"

        val newMemoSudoku = state.sudokuMemoBoard.map { it.toMutableList() }.toMutableList()
        newMemoSudoku[row][col] = ""

        reduce {
            state.copy(
                sudokuBoard = newSudoku,
                sudokuMemoBoard = newMemoSudoku
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

        saveData()

    }

    fun onNumberClick(number: Int) = intent {

        if(state.clickedPuzzle != "99") {
            val row = state.clickedPuzzle[0].digitToInt()
            val col = state.clickedPuzzle[1].digitToInt()

            val newSudoku = state.sudokuBoard.map { it.toMutableList() }.toMutableList()

            if(state.sudokuBoard[row][col] == number.toString()) {
                newSudoku[row][col] = "0"

                reduce {
                    state.copy(
                        sudokuBoard = newSudoku
                    )
                }

            } else {
                newSudoku[row][col] = number.toString()

                reduce {
                    state.copy(
                        sudokuBoard = newSudoku
                    )
                }

            }

            if (newSudoku.all { row -> row.all { it != "0" } }) {
                var success = 0
                // 0이 없으면 실행할 코드
                repeat(9) { index ->
                    val rowSum = newSudoku[index].sumOf { it.toInt() } // 각 문자(String)를 Int로 변환 후 합산
                    if (rowSum != 45) {
                        success++
                    }
                }

                repeat(9) { col ->
                    val colSum = newSudoku.sumOf { it[col].toInt() } // 각 열의 숫자를 Int로 변환 후 합산
                    if (colSum != 45) {
                        success++
                    }
                }

                if (success == 0) {
                    //성공
                    stopTimer()
                    sudokuDao.update(id = "state", value = "0" )

                    val plusLove = when(state.level) {
                        1 -> 60
                        2 -> 150
                        else -> 500
                    }
                    val updatePatData = state.patData
                    updatePatData.love = state.patData.love + plusLove
                    updatePatData.gameCount = state.patData.gameCount + 1
                    patDao.update(updatePatData)

                    userDao.update(
                        id = "money",
                        value2 = (state.userData.find { it.id == "money" }!!.value2.toInt() + plusLove).toString()
                    )

                    val current = state.userData.find { it.id == "thirdGame" }!!
                    when (state.level) {
                        1 -> userDao.update(id = "thirdGame", value = (current.value.toInt() + 1).toString())
                        2 -> userDao.update(id = "thirdGame", value2 = (current.value2.toInt() + 1).toString())
                        3 -> userDao.update(id = "thirdGame", value3 = (current.value3.toInt() + 1).toString())
                    }

                    reduce {
                        state.copy(
                            gameState = "성공",
                            plusLove = plusLove,
                            time = 0.0
                        )
                    }
                } else {
                    postSideEffect(ThirdGameSideEffect.Toast("오류가 있습니다."))
                }
            }
        }

        saveData()

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

        saveData()

    }

    private fun makeSudoku(zeroNumber: Int) = intent {
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
        positions.shuffled().take(zeroNumber).forEach { (row, col) ->
            board[row][col] = 0
        }

        // 기존 2차원 리스트를 문자열 리스트로 변환
        val newBoard: List<List<String>> = board.map { row -> row.map { it.toString() } }

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
            val startTime = SystemClock.elapsedRealtime() - sudokuDao.getValueById("time").toDouble()*1000 // 시작 시간 기록
            while (true) {
                val elapsed = (SystemClock.elapsedRealtime() - startTime) / 1000.0 // 경과 시간(초)
                intent {
                    reduce { state.copy(time = elapsed) } // 정확한 시간 반영
                }
                delay(10L) // 10ms마다 체크 (하지만 실제 시간은 SystemClock 기반)
            }
        }
    }

    fun onLevelClick(level: Int) = intent {

        when(level) {
            1 -> makeSudoku(30)
            2 -> makeSudoku(40)
            3 -> makeSudoku(50)
        }

        sudokuDao.update(id = "state", value = "1")
        sudokuDao.update(id = "level", value = level.toString())

        reduce {
            state.copy(
                gameState = "",
                level = level,
                time = 0.0
            )
        }

        startTimer()

        saveData()

    }

}


@Immutable
data class ThirdGameState(
    val userData: List<User> = emptyList(),
    val patData: Pat = Pat(url = ""),
    val sudokuBoard: List<List<String>> = List(9) { List(9) { "0" } }, // 9x9 스도쿠 보드 추가
    val sudokuFirstBoard: List<List<String>> = List(9) { List(9) { "0" } }, // 9x9 스도쿠 보드 추가
    val sudokuMemoBoard: List<List<String>> = List(9) { List(9) { "0" } }, // 9x9 메모 스도쿠 보드 추가
    val clickedPuzzle : String = "99",
    val time : Double = 0.0,
    val level : Int = 0,
    val gameState : String = "",
    val memoMode : Boolean = false,
    val plusLove : Int = 0,

    )


//상태와 관련없는 것
sealed interface ThirdGameSideEffect{
    class Toast(val message:String): ThirdGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}
