package com.a0100019.mypat.presentation.game.thirdGame

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
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

}


@Immutable
data class ThirdGameState(
    val id:String = "",
    val password:String = "",
    val userData: List<User> = emptyList()
)


//상태와 관련없는 것
sealed interface ThirdGameSideEffect{
    class Toast(val message:String): ThirdGameSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}
