package com.a0100019.mypat.presentation.game.firstGame

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
class FirstGameViewModel @Inject constructor(
    private val userDao: UserDao,
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

    }

    fun onGameStartClick(surfaceWidthDp: Dp, surfaceHeightDp:Dp) = intent {
        val snowballX = surfaceWidthDp * 0.5f - 15.dp // 가로의 50%
        val snowballY = surfaceHeightDp * 0.9f - 15.dp // 세로의 90%

        reduce {
            state.copy(
                snowballX = snowballX,
                snowballY = snowballY,
                surfaceWidthDp = surfaceWidthDp,
                surfaceHeightDp = surfaceHeightDp,
                shotStart = false
            )
        }
    }

    fun onMoveClick() = intent {
        var velocity = 100.dp // 초기 속도
        val rotationAngle = if(state.rotationAngle >= 0) state.rotationAngle%360f else (state.rotationAngle+3600f)%360f
        val decelerationFactor = 0.7f // 감속 계수 (0.9 = 10%씩 속도 감소)

        while (velocity > 5.dp) { // 속도가 dp 이하가 되면 정지
            var velocityX = 0.dp
            var velocityY = 0.dp
            if(rotationAngle in 0f..90f) {
                velocityX = velocity * (rotationAngle/90f)
                velocityY = -velocity * (90f - rotationAngle)/90f
            } else if (rotationAngle in 90f..180f) {
                velocityX = velocity * (180f - rotationAngle)/90f
                velocityY = -velocity * (90f - rotationAngle)/90f
            } else if (rotationAngle in 180f..270f) {
                velocityX = -velocity * (rotationAngle - 180f)/90f
                velocityY = -velocity * (rotationAngle - 270f)/90f
            } else {
                velocityX = velocity * (rotationAngle-360f)/90f
                velocityY = -velocity * (rotationAngle-270f)/90f
            }

            var nextX = state.snowballX + velocityX
            var nextY = state.snowballY + velocityY


            reduce {
                state.copy(
                    snowballX = state.snowballX + velocityX,
                    snowballY = state.snowballY + velocityY,
                    shotStart = true
                ) // 현재 속도로 이동
            }
            velocity *= decelerationFactor // 속도 감소
            delay(500) // 0.5초마다 실행 (더 부드럽게)
        }
    }

    fun onRotateRightClick() = intent {
        reduce {
            state.copy(rotationAngle = state.rotationAngle + 5f) // 5도씩 증가
        }
    }

    fun onRotateLeftClick() = intent {
        reduce {
            state.copy(rotationAngle = state.rotationAngle - 5f) // 5도씩 증가
        }
    }


}




@Immutable
data class FirstGameState(
    val id:String = "",
    val password:String = "",
    val userData: List<User> = emptyList(),
    val snowballX: Dp = 0.dp,
    val snowballY: Dp = 0.dp,
    val surfaceWidthDp: Dp = 0.dp,
    val surfaceHeightDp: Dp = 0.dp,
    val rotationAngle: Float = 0f,
    val shotStart: Boolean = false,
    val shotDuration: Int = 500
)


//상태와 관련없는 것
sealed interface FirstGameSideEffect{
    class Toast(val message:String): FirstGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}