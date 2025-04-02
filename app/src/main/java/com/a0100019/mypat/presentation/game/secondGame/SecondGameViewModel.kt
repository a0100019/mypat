package com.a0100019.mypat.presentation.game.secondGame

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.pet.PatDao
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
    private val patDao: PatDao,
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
        val userDataList = userDao.getAllUserData()
        val patData = patDao.getPatDataById(userDataList.find { it.id == "selectPat" }?.value ?: "0")
        reduce {
            state.copy(
                userData = userDataList,
                patData = patData
            )
        }
    }

    fun onGameStartClick() = intent {
        startTimer()
        val goalList: List<Int> = (1..5).shuffled().take(5)
        val randomNumbers = (0 until 25).shuffled().take(5)
        val targetList = state.targetList.toMutableStateList()
        repeat(5) {
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

    fun onGameReStartClick() = intent {
        reduce {
            state.copy(
                gameState = "시작",
                time = 0.00,
                targetList = List(25) {0},
                goalList = List(5) {0},
                level = 1,
                plusLove = 0
            )
        }
    }

    private fun stopTimer() {
        //타이머 종료
        timerJob?.cancel()
    }

    fun onNextLevelClick() = intent {
        if(state.level != 5){
            if (state.goalList[0] == 0) {
                val newLevel = state.level + 1

                val baseSet = (1..5).shuffled() // 한 번만 섞은 세트 생성
                val newGoalList: List<Int> = List(newLevel) { baseSet } // baseSet을 newLevel번 반복
                    .flatten() // 중첩 리스트를 평탄화

                val randomNumbers = (0 until 25).shuffled().take(5 * newLevel)
                val targetList = state.targetList.toMutableStateList()
                repeat(5 * newLevel) { it ->
                    targetList[randomNumbers[it]] = it % 5 + 1
                }

                reduce {
                    state.copy(
                        goalList = newGoalList,
                        targetList = targetList,
                        level = newLevel
                    )
                }
            }
        } else {
            val targetList = List(25) {6}
            reduce {
                state.copy(
                    goalList = targetList,
                    targetList = targetList,
                    level = 6,
                    gameState = "마지막"
                )
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun onFinishClick() = intent {
        if (state.goalList[0] == 0) {

            stopTimer()

            val plusLove = if(state.time < 500) {
                500 - state.time
            } else {
                10
            }.toInt()

            val updatePatData = state.patData
            updatePatData.love = state.patData.love + plusLove
            patDao.update(updatePatData)

            var gameState = "성공"
            val time = state.time
            val oldTime = state.userData.find {it.id == "secondGame"}?.value?.toDouble()

            if(time < oldTime!!) {
                gameState = "신기록"
                userDao.update(id = "secondGame", value = String.format("%.2f", time), value3 = state.patData.id.toString())
            }

            reduce {
                state.copy(
                    gameState = gameState,
                    plusLove = plusLove
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
            //다른거 클릭했을때
        }
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
data class SecondGameState(
    val userData: List<User> = emptyList(),
    val patData: Pat = Pat(url = ""),

    val time : Double = 0.00,
    val level : Int = 1,
    val gameState : String = "시작",
    val plusLove : Int = 0,

    val goalList : List<Int> = List(5) {0},
    val targetList : List<Int> = List(25) {0}

)


//상태와 관련없는 것
sealed interface SecondGameSideEffect{
    class Toast(val message:String): SecondGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}
