package com.a0100019.mypat.presentation.game.secondGame

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
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
                patData = patData,
                mapList = listOf(
                    "1300003030030300303000032", "2030200000331330000020302", "2000203330031300303020002", "2300000030333320003013000",
                    "2020202020201020202020202", "0202020202021202020202020", "2313203030030300000032323", "2303003000031300003003032",
                    "0323030003203023000303130", "0330230000000030000310030"
                ).shuffled(),
                time = 0.00,
                plusTime = 0.00,
                round = 0,
                plusLove = 0,
                gameState = "시작"

            )
        }
    }

    fun onGameStartClick() = intent {

        startTimer()

        reduce {
            state.copy(
                gameState = "진행",
            )
        }

    }

    fun onGameReStartClick() = intent {
        loadData()
    }

    fun onMoveClick(direction: String, repeatCount: Int = 0, moveCount: Int = 0) = intent {
        var nowMapList = state.mapList[state.round]

        //현재 나의 위치
        val index = nowMapList.indexOf('1')

        if(direction == "left") {

            if(index%5 != 0) {
                if(nowMapList[index-1] == '3') {
                    reduce {
                        state.copy(
                            plusTime = state.plusTime + 5
                        )
                    }
                }
                nowMapList = nowMapList.substring(0, index-1) + "10" + nowMapList.substring(index+1)
            }

        } else if(direction == "right") {

            if(index%5 != 4) {
                if(nowMapList[index+1] == '3') {
                    reduce {
                        state.copy(
                            plusTime = state.plusTime + 5
                        )
                    }
                }
                nowMapList = nowMapList.substring(0, index) + "01" + nowMapList.substring(index+2)
            }
        } else if(direction == "up") {

            if(index/5 != 0) {
                if(nowMapList[index-5] == '3') {
                    reduce {
                        state.copy(
                            plusTime = state.plusTime + 5
                        )
                    }
                }
                nowMapList = nowMapList.replace('1', '0')
                nowMapList = nowMapList.substring(0, index-5) + "1" + nowMapList.substring(index-4)
            }

        } else if(direction == "down") {

            if(index/5 != 4) {
                if(nowMapList[index+5] == '3') {
                    reduce {
                        state.copy(
                            plusTime = state.plusTime + 5
                        )
                    }
                }
                nowMapList = nowMapList.replace('1', '0')
                nowMapList = nowMapList.substring(0, index+5) + "1" + nowMapList.substring(index+6)
            }

        }

        val updateMapList = state.mapList.toMutableList().apply {
            this[state.round] = nowMapList
        }

        reduce {
            state.copy(
                mapList = updateMapList
            )
        }

        if(repeatCount == moveCount) {
            //모든 목표를 다 먹었을 때 다음 라운드로 가는 코드
            if ('2' !in nowMapList) {
                nextRound()
            }
        }

    }

    fun onFastMoveClick(direction: String) = intent {

        val oldMapList = state.mapList[state.round]

        //현재 나의 위치
        val index = oldMapList.indexOf('1')

        if(direction == "left") {

            val moveCount = index%5

            repeat(moveCount) {
                onMoveClick("left", it+1, moveCount)
            }

        } else if(direction == "right") {

            val moveCount = 4 - index%5

            repeat(moveCount) {
                onMoveClick("right", it+1, moveCount)
            }

        } else if(direction == "up") {

            val moveCount = index/5

            repeat(moveCount) {
                onMoveClick("up", it+1, moveCount)
            }

        } else if(direction == "down") {

            val moveCount = 4 - index/5

            repeat(moveCount) {
                onMoveClick("down", it+1, moveCount)
            }

        }

    }

    @SuppressLint("DefaultLocale")
    private fun nextRound() = intent {

        val oldRound = state.round

        if(oldRound != 9) {

            reduce {
                state.copy(
                    round = oldRound + 1,
                )
            }

        } else {

            stopTimer()

            val time = state.time + state.plusTime
            var plusLove = 20

            if(state.plusTime < 30) {

                val updatePatData = state.patData
                updatePatData.love = state.patData.love + 20
                updatePatData.gameCount = state.patData.gameCount + 1

                patDao.update(updatePatData)

                userDao.update(
                    id = "money",
                    value2 = (state.userData.find { it.id == "money" }!!.value2.toInt() + 20).toString()
                )

            } else {
                //막누르면 애정도 +5

                val updatePatData = state.patData
                updatePatData.love = state.patData.love + 5
                updatePatData.gameCount = state.patData.gameCount + 1

                patDao.update(updatePatData)

                userDao.update(
                    id = "money",
                    value2 = (state.userData.find { it.id == "money" }!!.value2.toInt() + 5).toString()
                )

                plusLove = 5

            }

            var gameState = "성공"
            val oldTime = state.userData.find {it.id == "secondGame"}?.value?.toDouble()

            if(time < oldTime!!) {
                gameState = "신기록"
                userDao.update(id = "secondGame", value = String.format("%.3f", time), value3 = state.patData.id.toString())
            }

            reduce {
                state.copy(
                    gameState = gameState,
                    plusLove = plusLove
                )
            }

        }

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
data class SecondGameState(
    val userData: List<User> = emptyList(),
    val patData: Pat = Pat(url = ""),

    val time : Double = 0.00,
    val plusTime : Double = 0.00,
    val round : Int = 0,
    val gameState : String = "시작",
    val plusLove : Int = 0,

    val mapList : List<String> = List(25) {""}

)


//상태와 관련없는 것
sealed interface SecondGameSideEffect{
    class Toast(val message:String): SecondGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}
