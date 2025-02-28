package com.a0100019.mypat.presentation.daily.walk

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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
class WalkViewModel @Inject constructor(
    private val userDao: UserDao,
    private val application: Application,
    private val stepCounterManager: StepCounterManager

) : ViewModel(), ContainerHost<WalkState, WalkSideEffect> {

    override val container: Container<WalkState, WalkSideEffect> = container(
        initialState = WalkState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(WalkSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    private val _todayWalk = MutableStateFlow(0)

//    // 뷰 모델 초기화 시 모든 user 데이터를 로드 및 걸음 수 추적 시작
//    init {
//        loadData()
//    }
//
//    private fun loadData() = intent {
//    }

//걸음수 멈추기 추가

    init {
        observeStepCount()
        stepCounterManager.startListening()
    }

    private fun observeStepCount() {
        viewModelScope.launch {
            stepCounterManager.stepCount.collectLatest { steps ->
                _todayWalk.value = steps // ✅ 올바르게 값 업데이트
                intent {
                    reduce { state.copy(todayWalk = steps) } // ✅ _todayWalk -> steps 값 사용
                }
            }
        }
    }

}


@Immutable
data class WalkState(
    val todayWalk: Int = 0, // ✅ 걸음 수 저장 (초기값 0)
    val userData: List<User> = emptyList()
)

sealed interface WalkSideEffect {
    class Toast(val message: String) : WalkSideEffect
}
