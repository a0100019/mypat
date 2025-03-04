package com.a0100019.mypat.presentation.daily.walk

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
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
    private val walkDao: WalkDao,
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




//걸음수 멈추기 추가

    init {
        observeStepCount()
        stepCounterManager.startListening()
        loadData()
    }

    private fun loadData() = intent {

        val userDataList = userDao.getAllUserData()
        val walkDataList = walkDao.getAllWalkData()

        reduce {
            state.copy(
                userDataList = userDataList,
                walkDataList = walkDataList
            )
        }

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

    fun changeChartMode(mode: String) = intent {

        if (mode == "주") {
            val weeksData = mutableListOf<Walk>()
            var weekIndex = 1

            // walkDataList를 7일씩 묶고, 각 묶음에 대해 평균을 계산
            state.walkDataList.chunked(7).map { weekData ->
                val averageCount = weekData.map { it.count }.average().toInt() // 평균을 Int로 변환

                val weekLabel = "${weekIndex}주 전" // 주 번호 설정

                // 첫 번째 날짜를 주 날짜로 사용하고, 평균 count를 저장
                weeksData.add(
                    Walk(
                        date = weekLabel,  // "1주 전", "2주 전" 등
                        count = averageCount  // 평균 count 값
                    )
                )
                weekIndex++
            }

            // reduce를 사용해 새로운 상태로 업데이트
            reduce {
                state.copy(
                    chartMode = "주",  // 차트 모드를 "주"로 변경
                    walkDataList = weeksData  // 주 단위로 묶인 데이터로 업데이트
                )
            }
        }

    }


}


@Immutable
data class WalkState(
    val todayWalk: Int = 0, // ✅ 걸음 수 저장 (초기값 0)
    val userDataList: List<User> = emptyList(),
    val walkDataList: List<Walk> = emptyList(),
    val chartMode: String = "일"
)

sealed interface WalkSideEffect {
    class Toast(val message: String) : WalkSideEffect
}
