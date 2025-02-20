package com.a0100019.mypat.presentation.daily.walk

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
class WalkViewModel @Inject constructor(
    private val userDao: UserDao,
    private val stepCounterManager: StepCounterManager

) : ViewModel(), ContainerHost<WalkState, WalkSideEffect> {

    override val container: Container<WalkState, WalkSideEffect> = container(
        initialState = WalkState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(WalkSideEffect.Toast(message = throwable.message.orEmpty()))
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

    fun startTracking() = intent {
        stepCounterManager.startListening()
    }

    fun stopTracking() = intent {
        stepCounterManager.stopListening()
    }

}




@Immutable
data class WalkState(
    val todayWalk : Int = 0,
    val userData: List<User> = emptyList()
)


//상태와 관련없는 것
sealed interface WalkSideEffect{
    class Toast(val message:String): WalkSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}