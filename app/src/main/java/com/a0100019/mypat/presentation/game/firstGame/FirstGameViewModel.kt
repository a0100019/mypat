package com.a0100019.mypat.presentation.game.firstGame

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.pet.PatDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
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
import kotlin.math.sqrt
import kotlin.random.Random


@HiltViewModel
class FirstGameViewModel @Inject constructor(
    private val userDao: UserDao,
    private val patDao: PatDao
) : ViewModel(), ContainerHost<FirstGameState, FirstGameSideEffect> {

    override val container: Container<FirstGameState, FirstGameSideEffect> = container(
        initialState = FirstGameState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(FirstGameSideEffect.Toast(message = throwable.message.orEmpty()))
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

    fun onGameStartClick(surfaceWidthDp: Dp, surfaceHeightDp:Dp) = intent {
        val snowballX = surfaceWidthDp * 0.5f - state.snowballSize/2 // 가로의 50%
        val snowballY = surfaceHeightDp * 0.9f - state.snowballSize/2 // 세로의 90%
        val targetX = surfaceWidthDp * 0.5f - state.targetSize/2
        val targetY = surfaceHeightDp * 0.2f - state.targetSize/2

        reduce {
            state.copy(
                snowballX = snowballX,
                snowballY = snowballY,
                surfaceWidthDp = surfaceWidthDp,
                surfaceHeightDp = surfaceHeightDp,
                targetX = targetX,
                targetY = targetY,
                score = 0,
                level = 1,
                situation = "준비"
            )
        }
    }

    fun onGameReStartClick() = intent {
        val snowballX = state.surfaceWidthDp * 0.5f - state.snowballSize/2 // 가로의 50%
        val snowballY = state.surfaceHeightDp * 0.9f - state.snowballSize/2 // 세로의 90%
        val targetX = state.surfaceWidthDp * 0.5f - state.targetSize/2
        val targetY = state.surfaceHeightDp * 0.2f - state.targetSize/2

        val patData = patDao.getPatDataById(state.patData.id.toString())

        reduce {
            state.copy(
                snowballX = snowballX,
                snowballY = snowballY,
                targetX = targetX,
                targetY = targetY,
                score = 0,
                level = 1,
                situation = "준비",
                rotationAngle = 0f,
                patData = patData
            )
        }
    }

    fun onMoveClick() = intent {
        reduce {
            state.copy(
                situation = "발사중",
                isShotSetting = false
            )
        }
        val velocity = state.snowballSpeed // 초기 속도
        val rotationAngle = if(state.rotationAngle >= 0) state.rotationAngle%360f else (state.rotationAngle+3600f)%360f

        var totalVelocityX = 0.dp
        var totalVelocityY = 0.dp

        if(rotationAngle in 0f..90f) {
            totalVelocityX = velocity * (rotationAngle/90f)
            totalVelocityY = -velocity * (90f - rotationAngle)/90f
        } else if (rotationAngle in 90f..180f) {
            totalVelocityX = velocity * (180f - rotationAngle)/90f
            totalVelocityY = -velocity * (90f - rotationAngle)/90f
        } else if (rotationAngle in 180f..270f) {
            totalVelocityX = -velocity * (rotationAngle - 180f)/90f
            totalVelocityY = -velocity * (rotationAngle - 270f)/90f
        } else {
            totalVelocityX = velocity * (rotationAngle-360f)/90f
            totalVelocityY = -velocity * (rotationAngle-270f)/90f
        }

        totalVelocityX /= 1000
        totalVelocityY /= 1000


        viewModelScope.launch {
            repeat(1000) { iteration ->
                if (iteration>500){
                    val dynamicDelay =
                        (1 + ((iteration-500) / 30)).toLong() // 1~10ms 증가
                    delay(dynamicDelay)
                    reduce {
                        state.copy(shotDuration = dynamicDelay.toInt())
                    }
                } else {
                    val dynamicDelay =
                        (1).toLong() // 1~10ms 증가
                    delay(dynamicDelay)
                    reduce {
                        state.copy(shotDuration = dynamicDelay.toInt())
                    }
                }

                if (state.snowballX + state.snowballSize >= state.surfaceWidthDp) {
                    reduce {
                        state.copy(snowballX = state.snowballX - totalVelocityX)
                    }
                    totalVelocityX = -totalVelocityX

                } else if (state.snowballX <= 0.dp) {
                    reduce {
                        state.copy(snowballX = state.snowballX - totalVelocityX)
                    }
                    totalVelocityX = -totalVelocityX

                } else {
                    if (totalVelocityX > 0.dp) {
                        reduce {
                            state.copy(snowballX = state.snowballX + totalVelocityX)
                        }

                    } else {
                        reduce {
                            state.copy(snowballX = state.snowballX + totalVelocityX)
                        }

                    }
                }
            }
        }

        viewModelScope.launch {
            repeat(1000) { iteration ->
                if (iteration>800){
                    val dynamicDelay =
                        (17 + ((iteration-800) / 10)).toLong() // 1~10ms 증가
                    delay(dynamicDelay)
                    reduce {
                        state.copy(shotDuration = dynamicDelay.toInt())
                    }
                } else if(iteration>600) {
                    val dynamicDelay =
                        (7 + ((iteration-600) / 20)).toLong() // 1~10ms 증가
                    delay(dynamicDelay)
                    reduce {
                        state.copy(shotDuration = dynamicDelay.toInt())
                    }
                } else if(iteration>400) {
                    val dynamicDelay =
                        (3 + ((iteration-400) / 50)).toLong() // 1~10ms 증가
                    delay(dynamicDelay)
                    reduce {
                        state.copy(shotDuration = dynamicDelay.toInt())
                    }
                } else if(iteration>200) {
                    val dynamicDelay =
                        (1 + ((iteration-200) / 100)).toLong() // 1~10ms 증가
                    delay(dynamicDelay)
                    reduce {
                        state.copy(shotDuration = dynamicDelay.toInt())
                    }
                } else {
                    val dynamicDelay =
                        (1).toLong() // 1~10ms 증가
                    delay(dynamicDelay)
                    reduce {
                        state.copy(shotDuration = dynamicDelay.toInt())
                    }
                }

                if (state.snowballY + state.snowballSize >= state.surfaceHeightDp) { // 아래 벽 충돌
                    reduce {
                        state.copy(snowballY = state.snowballY - totalVelocityY)
                    }
                    totalVelocityY = -totalVelocityY // 방향 반전

                } else if (state.snowballY <= 0.dp) { // 위쪽 벽 충돌
                    reduce {
                        state.copy(snowballY = state.snowballY - totalVelocityY)
                    }
                    totalVelocityY = -totalVelocityY // 방향 반전

                } else {
                    if (totalVelocityY > 0.dp) { // 아래로 이동 중
                        reduce {
                            state.copy(snowballY = state.snowballY + totalVelocityY)
                        }
                    } else { // 위로 이동 중
                        reduce {
                            state.copy(snowballY = state.snowballY + totalVelocityY)
                        }
                    }
                }
            }

            delay(1000)
            //공 이동 끝
            val distance = sqrt(
                (state.targetX - state.snowballX).value * (state.targetX - state.snowballX).value +
                        (state.targetY - state.snowballY).value * (state.targetY - state.snowballY).value
            )

            if(distance < 200) {
                reduce {
                    state.copy(
                        score = state.score + 200 - distance.toInt(),
                        situation = "다음"
                    )
                }

            } else {

                val updatePatData = state.patData
                updatePatData.love = state.patData.love + state.score
                patDao.update(updatePatData)

                if(state.userData.find { it.id == "curling" }!!.value.toInt() < state.score){
                    userDao.update(id = "curling", value = state.score.toString())
                    reduce {
                        state.copy(
                            situation = "신기록"
                        )
                    }
                } else {
                    reduce {
                        state.copy(
                            situation = "종료"
                        )
                    }
                }

            }


        }


    }

    fun onNextLevelClick() = intent {
        val randomX = Random.nextInt(state.targetSize.value.toInt(), (state.surfaceWidthDp - state.targetSize).value.toInt())
        val randomY = Random.nextInt(state.targetSize.value.toInt(), (state.surfaceHeightDp - state.targetSize).value.toInt())

        reduce {
            state.copy(
                situation = "회전",
                level = state.level + 1,
                targetX = randomX.dp,
                targetY = randomY.dp,
                isRotating = true
            )
        }


        viewModelScope.launch {
            while (state.isRotating) { // isActive를 체크하여 안전하게 종료 가능
                reduce {
                    state.copy(rotationAngle = state.rotationAngle + 1f)
                }
                delay(state.rotationDuration.toLong() - state.level) // 회전속도
            }
        }


    }

    fun onRotateStopClick() = intent {
        reduce {
            state.copy(
                isRotating = false,
                isShotSetting = true,
                situation = "준비"
            )
        }

        while (state.isShotSetting) { // isActive를 체크하여 안전하게 종료 가능

            if(state.shotPower < 1000) {
                reduce {
                    state.copy(shotPower = state.shotPower + 10)
                }
            } else {
                reduce {
                    state.copy(shotPower = 10)
                }
            }

            delay((state.rotationDuration.toLong() - state.level)*10) //
        }
    }



}



@Immutable
data class FirstGameState(
    val userData: List<User> = emptyList(),
    val snowballX: Dp = 0.dp,
    val snowballY: Dp = 0.dp,
    val surfaceWidthDp: Dp = 0.dp,
    val surfaceHeightDp: Dp = 0.dp,
    val rotationAngle: Float = 0f,
    val situation: String = "시작",
    val shotDuration: Int = 1,
    val snowballSize: Dp = 30.dp,
    val snowballSpeed: Dp = 500.dp,
    val targetSize: Dp = 100.dp,
    val targetX: Dp = 0.dp,
    val targetY: Dp = 0.dp,
    val score: Int = 0,
    val level: Int = 1,
    val isRotating: Boolean = false,
    val isShotSetting: Boolean = false,
    val rotationDuration: Int = 10,
    val shotPower: Int = 10,
    val patData: Pat = Pat(url = "")
)


//상태와 관련없는 것
sealed interface FirstGameSideEffect{
    class Toast(val message:String): FirstGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}