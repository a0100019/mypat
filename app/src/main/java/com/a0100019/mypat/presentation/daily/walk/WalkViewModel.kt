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
    val todayWalk: StateFlow<Int> = _todayWalk

//    // 뷰 모델 초기화 시 모든 user 데이터를 로드 및 걸음 수 추적 시작
//    init {
//        loadData()
//    }
//
//    private fun loadData() = intent {
//    }


    init {
//        startForegroundService() // ✅ 앱이 실행될 때 자동으로 Foreground Service 시작
//        observeSteps() // ✅ 걸음 수 감지
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

//
//    // ✅ 걸음 수 데이터 감지 및 업데이트
//    private fun observeSteps() {
//        viewModelScope.launch {
//            StepCounterService.stepsFlow.collectLatest { stepCount ->
//                intent {
//                    reduce { state.copy(todayWalk = stepCount) }
//                }
//                Log.d("WalkViewModel", "✅ 걸음 수 업데이트됨: $stepCount")
//            }
//        }
//    }
//
//    fun startForegroundService() {
//        Log.d("WalkViewModel", "✅ Foreground Service 시작 버튼 클릭됨")
//
//        val intent = Intent(application, StepCounterService::class.java)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            application.startForegroundService(intent)
//            Log.d("WalkViewModel", "✅ startForegroundService 호출됨")
//        } else {
//            application.startService(intent)
//            Log.d("WalkViewModel", "✅ startService 호출됨")
//        }
//    }
//
//
//    // ✅ Foreground Service 중지
//    fun stopForegroundService() {
//        Log.d("WalkViewModel", "🚨 Foreground Service 중지 버튼 클릭됨")
//        val intent = Intent(application, StepCounterService::class.java)
//        application.stopService(intent)
//    }
}


@Immutable
data class WalkState(
    val todayWalk: Int = 0, // ✅ 걸음 수 저장 (초기값 0)
    val userData: List<User> = emptyList()
)

sealed interface WalkSideEffect {
    class Toast(val message: String) : WalkSideEffect
}
