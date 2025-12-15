package com.a0100019.mypat.presentation.game.secondGame

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.pat.PatDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.presentation.main.MainSideEffect
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
                firstNumberList = state.firstNumberList.shuffled(),
                secondNumberList = state.secondNumberList.shuffled(),
                stateList = MutableList(25) { "0" },
                time = 0.00,
                plusTime = 0.00,
                targetNumber = 1,
                plusLove = 0,
                gameState = "시작"

            )
        }
    }

    private fun onGameStart() = intent {

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

    fun onIndexClick(clickIndex: Int) = intent {

        val firstNumberList = state.firstNumberList
        val secondNumberList = state.secondNumberList
        val stateList = state.stateList
        var targetNumber = state.targetNumber

        if(stateList[clickIndex] == "0") {

            if(firstNumberList[clickIndex] == targetNumber) {

                if(targetNumber == 1) {
                    onGameStart()
                }

                stateList[clickIndex] = "1"
                targetNumber += 1
            }

        } else if(stateList[clickIndex] == "1") {

            if(secondNumberList[clickIndex] == targetNumber) {
                stateList[clickIndex] = "2"
                targetNumber += 1
            }

        }

        if(targetNumber == 51) {
            gameOver()
        }

        reduce {
            state.copy(
                stateList = stateList,
                targetNumber = targetNumber
            )
        }

    }

    @SuppressLint("DefaultLocale")
    private fun gameOver() = intent {

        stopTimer()

        val time = state.time + state.plusTime
        val plusLove = 100

        val updatePatData = state.patData
        updatePatData.love = state.patData.love + plusLove
        updatePatData.gameCount = state.patData.gameCount + 1

        if(state.patData.gameCount + 1 >= 100) {

            //매달, medal, 칭호4
            val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

            val myMedalList: MutableList<Int> =
                myMedal
                    .split("/")
                    .mapNotNull { it.toIntOrNull() }
                    .toMutableList()

            // 🔥 여기 숫자 두개 바꾸면 됨
            if (!myMedalList.contains(4)) {
                myMedalList.add(4)

                // 다시 문자열로 합치기
                val updatedMedal = myMedalList.joinToString("/")

                // DB 업데이트
                userDao.update(
                    id = "etc",
                    value3 = updatedMedal
                )

                postSideEffect(SecondGameSideEffect.Toast("칭호를 획득했습니다!"))
            }

        }

        patDao.update(updatePatData)

        userDao.update(
            id = "money",
            value2 = (state.userData.find { it.id == "money" }!!.value2.toInt() + plusLove).toString()
        )

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
    val targetNumber : Int = 1,
    val gameState : String = "시작",
    val plusLove : Int = 0,

    val firstNumberList: List<Int> = (1..25).shuffled(),
    val secondNumberList: List<Int> = (26..50).shuffled(),
    val stateList: MutableList<String> = MutableList(25) { "0" }


)


//상태와 관련없는 것
sealed interface SecondGameSideEffect{
    class Toast(val message:String): SecondGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}
