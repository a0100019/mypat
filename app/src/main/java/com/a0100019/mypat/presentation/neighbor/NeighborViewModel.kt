package com.a0100019.mypat.presentation.neighbor

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
class NeighborViewModel @Inject constructor(
    private val userDao: UserDao,
) : ViewModel(), ContainerHost<NeighborState, NeighborSideEffect> {

    override val container: Container<NeighborState, NeighborSideEffect> = container(
        initialState = NeighborState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(NeighborSideEffect.Toast(message = throwable.message.orEmpty()))
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

    fun onClose() = intent {
        reduce {
            state.copy(

            )
        }
    }

}


@Immutable
data class NeighborState(
    val userData: List<User> = emptyList()
)


//상태와 관련없는 것
sealed interface NeighborSideEffect{
    class Toast(val message:String): NeighborSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}