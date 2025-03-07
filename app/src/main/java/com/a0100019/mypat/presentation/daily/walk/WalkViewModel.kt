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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
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
        val totalWalkCount = walkDataList.sumOf { it.count }
        val maxWalkCount = walkDataList.maxOfOrNull { it.count } ?: 0
        val goalCount = walkDataList.count { it.count >= 10000 }
        val todayDropWalkDataList = walkDataList.drop(1) // 최신 데이터 제외

        val walkWeeksDataList =walkDao.getAllWalkData()
        val recentWalkData = walkWeeksDataList
            .drop(1) // 최신 데이터 제외
            .take(7) // 최근 7개만 가져오기
            .map { walkData ->
                walkData.copy( // 기존 데이터를 수정하여 새로운 리스트 생성
                    date = try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
                        val date = inputFormat.parse(walkData.date)
                        outputFormat.format(date ?: walkData.date)
                    } catch (e: Exception) {
                        walkData.date // 변환 실패 시 원본 유지
                    }
                )
            }
            .reversed()

        val firstData = walkDao.getLatestWalkData()
        val currentStepCount = stepCounterManager.getStepCount()
        val count = if (firstData.steps < currentStepCount) {
            currentStepCount - firstData.steps
        } else {
            currentStepCount
        }

        walkDao.updateCountByDate(date = firstData.date, newCount = firstData.count + count)
        walkDao.updateStepsByDate(date = firstData.date, newSteps = currentStepCount)

        reduce {
            state.copy(
                userDataList = userDataList,
                walkDataList = todayDropWalkDataList,
                walkWeeksDataList = recentWalkData,
                totalWalkCount = totalWalkCount,
                maxWalkCount = maxWalkCount,
                goalCount = goalCount,
                firstData = firstData

            )
        }

    }


    private fun observeStepCount() {
        viewModelScope.launch {
            stepCounterManager.stepCount.collectLatest { steps ->
                _todayWalk.value = steps // ✅ 올바르게 값 업데이트

                intent {
                    reduce { state.copy(todayWalk = steps - state.firstData.steps + state.firstData.count) } // ✅ _todayWalk -> steps 값 사용
                }
            }
        }
    }

    fun changeChartMode(mode: String) = intent {

        when (mode) {
            "일" -> {
                val walkDataList = walkDao.getAllWalkData().drop(1) // 최신 데이터 제외

                reduce {
                    state.copy(
                        chartMode = "일",
                        walkDataList = walkDataList
                    )
                }
            }
            "주" -> {
                val weeksData = mutableListOf<Walk>()
                var weekIndex = 1

                val walkDataList = walkDao.getAllWalkData().drop(1) // 최신 데이터 제외

                // walkDataList를 7일씩 묶고, 각 묶음에 대해 평균을 계산
                walkDataList.chunked(7).map { weekData ->
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
            else -> { //월별 통계
                val monthsData = mutableListOf<Walk>()
                var monthsIndex = 1

                val walkDataList = walkDao.getAllWalkData().drop(1) // 최신 데이터 제외
                // walkDataList를 7일씩 묶고, 각 묶음에 대해 평균을 계산
                walkDataList.chunked(30).map { weekData ->
                    val averageCount = weekData.map { it.count }.average().toInt() // 평균을 Int로 변환

                    val weekLabel = "${monthsIndex}달 전" // 주 번호 설정

                    monthsData.add(
                        Walk(
                            date = weekLabel,  // "1주 전", "2주 전" 등
                            count = averageCount  // 평균 count 값
                        )
                    )
                    monthsIndex++
                }
                reduce {
                    state.copy(
                        chartMode = "월",  // 차트 모드를 "주"로 변경
                        walkDataList = monthsData  // 주 단위로 묶인 데이터로 업데이트
                    )
                }
            }
        }

    }


}


@Immutable
data class WalkState(
    val userDataList: List<User> = emptyList(),
    val walkDataList: List<Walk> = emptyList(),
    val walkWeeksDataList: List<Walk> = emptyList(),

    val chartMode: String = "일",
    val todayWalk: Int = 0, // ✅ 걸음 수 저장 (초기값 0)
    val totalWalkCount: Int = 0,
    val maxWalkCount: Int = 0,
    val goalCount: Int = 0,
    val firstData: Walk = Walk(date = "")

)

sealed interface WalkSideEffect {
    class Toast(val message: String) : WalkSideEffect
}
