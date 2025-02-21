package com.a0100019.mypat.presentation.daily.walk

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val application: Application
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

    // 뷰 모델 초기화 시 모든 user 데이터를 로드 및 걸음 수 추적 시작
    init {
        loadData()
    }

    private fun loadData() = intent {
    }


    private val _stepCount = MutableStateFlow<Int>(0) // 현재 걸음 수
    val moveStepCount = _stepCount.asStateFlow()

    private val _todayStepCount = MutableStateFlow<Int>(0) // 누적 걸음 수
    val todayStepCount = _todayStepCount.asStateFlow()

    fun setTodayStepCount(newStepCount: Int) {
        _todayStepCount.value = newStepCount
    } // End of setStepCount()

    fun setStepCount(newStepCount: Int) {
        _stepCount.value = newStepCount
    } // End of setStepCount()



}

@Immutable
data class WalkState(
    val todayWalk: Int = 0,  // ✅ 걸음 수 저장
    val userData: List<User> = emptyList()
)

sealed interface WalkSideEffect {
    class Toast(val message: String) : WalkSideEffect
}
