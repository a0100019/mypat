package com.a0100019.mypat.presentation.game.secondGame

import androidx.compose.runtime.toMutableStateList
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
class SecondGameViewModel @Inject constructor(
    private val userDao: UserDao,
) : ViewModel(), ContainerHost<SecondGameState, SecondGameSideEffect> {

    override val container: Container<SecondGameState, SecondGameSideEffect> = container(
        initialState = SecondGameState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(SecondGameSideEffect.Toast(message = throwable.message.orEmpty()))
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

    fun onGameStartClick() = intent {
        startTimer()
        val goalList: List<Int> = (1..5).shuffled().take(5)
        val randomNumbers = (0 until 25).shuffled().take(5)
        val targetList = state.targetList.toMutableStateList()
        repeat(5) { it ->
            targetList[randomNumbers[it]] = it+1
        }

        reduce {
            state.copy(
                goalList = goalList,
                targetList = targetList,
                gameState = "진행"
            )
        }
    }

    fun gameOver() = intent {
        stopTimer()
    }

    fun onNextLevelClick() = intent {
        if(state.goalList[0] == 0) {
            val newLevel = state.level + 1

            val newGoalList: List<Int> = (1..5)
                .shuffled() // 먼저 숫자의 순서를 섞음
                .flatMap { num -> List(newLevel) { num } } // 이후 각 숫자를 newLevel번 반복

            val randomNumbers = (0 until 25).shuffled().take(5 * newLevel)
            val targetList = state.targetList.toMutableStateList()
            repeat(5 * newLevel) { it ->
                targetList[randomNumbers[it]] = it%5 + 1
            }

            reduce {
                state.copy(
                    goalList = newGoalList,
                    targetList = targetList,
                    level = newLevel
                )
            }
        }
    }

    fun onItemSelected(item : Int) = intent {
        if (state.targetList[item] == state.goalList.getOrNull(0)) {
            val newTargetList = state.targetList.mapIndexed { index, value ->
                if (index == item) 0 else value // 선택된 위치만 변경
            }

            val newGoalList = state.goalList.drop(1).toMutableList()
            newGoalList.add(0)

            reduce {
                state.copy(
                    targetList = newTargetList,
                    goalList = newGoalList
                )
            }
        } else {
            gameOver()
            reduce {
                state.copy(
                    gameState = "종료"
                )
            }
        }
    }



    // 타이머 시작
    private var timerJob: Job? = null
    fun startTimer() {
        timerJob?.cancel() // 기존 타이머 중지
        timerJob = viewModelScope.launch {
            while (true) {
                delay(100L) // 1초마다 실행
                intent {
                    reduce { state.copy(time = state.time + 0.1) } // time 증가
                }
            }
        }
    }

    // 타이머 중지
    fun stopTimer() {
        timerJob?.cancel()
    }

}




@Immutable
data class SecondGameState(
    val score : Int = 0,
    val time : Double = 0.00,
    val level : Int = 1,
    val gameState : String = "시작",

    val goalList : List<Int> = List(5) {0},
    val targetList : List<Int> = List(25) {0}

)


//상태와 관련없는 것
sealed interface SecondGameSideEffect{
    class Toast(val message:String): SecondGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}
