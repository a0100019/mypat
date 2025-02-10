package com.a0100019.mypat.presentation.game.firstGame

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
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
                surfaceHeightDp = surfaceHeightDp
            )
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
    val surfaceHeightDp: Dp = 0.dp
)


//상태와 관련없는 것
sealed interface FirstGameSideEffect{
    class Toast(val message:String): FirstGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}