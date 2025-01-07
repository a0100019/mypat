package com.a0100019.mypat.presentation.main


import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.presentation.main.world.WorldState
import com.a0100019.mypat.presentation.main.world.WorldViewModel
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
class MainViewModel @Inject constructor(
    private val userDao: UserDao,

) : ViewModel(), ContainerHost<MainState, MainSideEffect> {

    override val container: Container<MainState, MainSideEffect> = container(
        initialState = MainState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(MainSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadUserData()
    }

    //room에서 데이터 가져옴
    private fun loadUserData() = intent {
        // Flow 데이터를 State로 업데이트
        userDao.getAllUserData().collect { userData ->
            reduce {
                state.copy(userData = userData)
            }
        }

    }

    fun onWorldChangeClick() = intent {
        reduce {
            state.copy(worldChange = !state.worldChange) // true/false 토글
        }
    }

    fun onWorldAddClick() = intent {

    }

    fun updateWorldChange() = intent {
        reduce {
            state.copy(worldChange = !state.worldChange)
        }
    }

}

@Immutable
data class MainState(
    val userData: List<User> = emptyList(),
    val worldChange: Boolean = false,
)


//상태와 관련없는 것
sealed interface MainSideEffect{
    class Toast(val message:String): MainSideEffect

}