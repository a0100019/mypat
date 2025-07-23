package com.a0100019.mypat.presentation.daily.walk

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.presentation.daily.korean.KoreanSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val userDao: UserDao,
    private val walkDao: WalkDao,
    private val stepCounterManager: StepCounterManager
) : ViewModel(), ContainerHost<WalkState, WalkSideEffect> {

    override val container: Container<WalkState, WalkSideEffect> = container(
        initialState = WalkState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    Log.e("WalkViewModel", "Coroutine exception: ${throwable.message}")
                    postSideEffect(WalkSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    private val _todayWalk = MutableStateFlow(0)
    private var hasLoadedInitialData = false

    init {
        Log.d("WalkViewModel", "init - stepCounterManager startListening 호출")
        stepCounterManager.startListening()
        observeStepCount()
    }

    override fun onCleared() {
        super.onCleared()
        stepCounterManager.stopListening() // ✅ 뷰모델 종료 시 호출됨
    }

    private fun observeStepCount() {
        viewModelScope.launch {
            stepCounterManager.stepCount.collectLatest { steps ->
                Log.d("WalkViewModel", "stepCount Flow에서 받은 값: $steps")
                _todayWalk.value = steps

                if (!hasLoadedInitialData && steps > 0) {
                    Log.d("WalkViewModel", "초기 데이터 로드 조건 만족, loadData 호출")
                    hasLoadedInitialData = true
                    loadData()

                    val systemWalk = userDao.getValue2ById(id = "walk")
                    if(systemWalk == "0" ) {
                        userDao.update(id = "walk", value2 = steps.toString())
                    }

                    return@collectLatest
                }

                if (hasLoadedInitialData) {
                    intent {
                        val walkData = state.userDataList.find { it.id == "walk" }
                        Log.d("WalkViewModel", "걸음 수 갱신, 기존값: ${walkData?.value}, 기준값: ${walkData?.value2}, 현재 steps: $steps")
                        reduce {
                            state.copy(todayWalk = steps - walkData!!.value2.toInt() + walkData.value.toInt())
                        }
                    }
                }
            }
        }
    }

    private fun loadData() = intent {
        Log.d("WalkViewModel", "loadData 호출")
        val userDataList = userDao.getAllUserData()
        val walkDataList = walkDao.getAllWalkData()
        val walkUserData = userDataList.find { it.id == "walk" }
        Log.d("WalkViewModel", "loadData - walkUserData: $walkUserData")

        val totalWalkCount = walkUserData!!.value3
        val goalCount = walkDataList.count{it.success == "1"}

        val currentStepCount = stepCounterManager.getStepCount()
        Log.d("WalkViewModel", "loadData - 현재 stepCounterManager 걸음 수: $currentStepCount")

        val count = if (walkUserData.value2.toInt() <= currentStepCount) {
            currentStepCount - walkUserData.value2.toInt()
        } else {
            currentStepCount
        }

        userDao.update(
            id = "walk",
            value = (walkUserData.value.toInt() + count).toString(),
            value2 = currentStepCount.toString(),
            value3 = (walkUserData.value3.toInt() + count).toString()
        )
        Log.d("WalkViewModel", "loadData - userDao update 완료, count: $count")

        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val walkState = when(walkDataList.find { it.date == currentDate }?.success) {
            "0" -> "대기"
            else -> "완료"
        }
        Log.d("WalkViewModel", "현재 날짜: $currentDate, walkState: $walkState")

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val successDates = walkDataList
            .filter { it.success == "1" }
            .mapNotNull { runCatching { LocalDate.parse(it.date, formatter) }.getOrNull() }
            .sorted()
        var maxStreak = 0
        var currentStreak = 0
        var previousDate: LocalDate? = null
        for (date in successDates) {
            if (previousDate == null) {
                currentStreak = 1
            } else if (previousDate.plusDays(1) == date) {
                currentStreak++
            } else {
                currentStreak = 1
            }
            if (currentStreak > maxStreak) {
                maxStreak = currentStreak
            }
            previousDate = date
        }
        Log.d("WalkViewModel", "최대 연속 성공 횟수: $maxStreak")

        val firstDate = userDataList.find { it.id == "date" }!!.value3
        val inputDate = LocalDate.parse(firstDate, formatter)
        val today = LocalDate.now()
        val daysDiff = ChronoUnit.DAYS.between(inputDate, today) + 1
        val successRatio = goalCount * 100 / daysDiff
        Log.d("WalkViewModel", "성공률 계산 - 목표 성공 횟수: $goalCount, 총 일수: $daysDiff, 성공률: $successRatio")

        reduce {
            state.copy(
                userDataList = userDataList,
                walkDataList = walkDataList,
                totalWalkCount = totalWalkCount,
                totalSuccessCount = goalCount,
                todayWalk = walkUserData.value.toInt() + count,
                today = currentDate,
                calendarMonth = currentDate.substring(0, 7),
                walkState = walkState,
                maxContinuous = maxStreak,
                successRate = successRatio.toInt()
            )
        }
    }

    fun onTodayWalkSubmitClick() = intent {

        if(state.todayWalk >= 10000){

            userDao.update(
                id = "walk",
                value = (state.todayWalk-10000).toString(),
            )

            //보상
            userDao.update(
                id = "money",
                value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + 100).toString()
            )

            val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            walkDao.updateSuccessByDate(date = currentDate, success = "1")

            postSideEffect(WalkSideEffect.Toast("미션 완료 money+100"))

            loadData()
        } else {
            postSideEffect(WalkSideEffect.Toast("걸음 수가 부족합니다"))
        }

    }

    fun onCalendarMonthChangeClick(direction: String) = intent {

        val oldMonth = state.calendarMonth // 예: "2025-04"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
        val yearMonth = YearMonth.parse(oldMonth, formatter)

        val newYearMonth = when (direction) {
            "left" -> yearMonth.minusMonths(1)
            "right" -> yearMonth.plusMonths(1)
            else -> yearMonth
        }

        val newMonth = newYearMonth.format(formatter)
        if(direction == "today"){
            reduce {
                state.copy(
                    calendarMonth = state.today.substring(0, 7)
                )
            }
        } else {
            reduce {
                state.copy(
                    calendarMonth = newMonth
                )
            }
        }

    }

    fun changeChartMode(mode: String) = intent {
//
//        when (mode) {
//            "일" -> {
//                val walkDataList = walkDao.getAllWalkData().drop(1) // 최신 데이터 제외
//
//                reduce {
//                    state.copy(
//                        chartMode = "일",
//                        walkDataList = walkDataList
//                    )
//                }
//            }
//            "주" -> {
//                val weeksData = mutableListOf<Walk>()
//                var weekIndex = 1
//
//                val walkDataList = walkDao.getAllWalkData().drop(1) // 최신 데이터 제외
//
//                // walkDataList를 7일씩 묶고, 각 묶음에 대해 평균을 계산
//                walkDataList.chunked(7).map { weekData ->
//                    val averageCount = weekData.map { it.count }.average().toInt() // 평균을 Int로 변환
//
//                    val weekLabel = "${weekIndex}주 전" // 주 번호 설정
//
//                    // 첫 번째 날짜를 주 날짜로 사용하고, 평균 count를 저장
//                    weeksData.add(
//                        Walk(
//                            date = weekLabel,  // "1주 전", "2주 전" 등
//                            count = averageCount  // 평균 count 값
//                        )
//                    )
//                    weekIndex++
//                }
//
//                // reduce를 사용해 새로운 상태로 업데이트
//                reduce {
//                    state.copy(
//                        chartMode = "주",  // 차트 모드를 "주"로 변경
//                        walkDataList = weeksData  // 주 단위로 묶인 데이터로 업데이트
//                    )
//                }
//            }
//            else -> { //월별 통계
//                val monthsData = mutableListOf<Walk>()
//                var monthsIndex = 1
//
//                val walkDataList = walkDao.getAllWalkData().drop(1) // 최신 데이터 제외
//                // walkDataList를 7일씩 묶고, 각 묶음에 대해 평균을 계산
//                walkDataList.chunked(30).map { weekData ->
//                    val averageCount = weekData.map { it.count }.average().toInt() // 평균을 Int로 변환
//
//                    val weekLabel = "${monthsIndex}달 전" // 주 번호 설정
//
//                    monthsData.add(
//                        Walk(
//                            date = weekLabel,  // "1주 전", "2주 전" 등
//                            count = averageCount  // 평균 count 값
//                        )
//                    )
//                    monthsIndex++
//                }
//                reduce {
//                    state.copy(
//                        chartMode = "월",  // 차트 모드를 "주"로 변경
//                        walkDataList = monthsData  // 주 단위로 묶인 데이터로 업데이트
//                    )
//                }
//            }
//        }

    }
}

@Immutable
data class WalkState(
    val userDataList: List<User> = emptyList(),
    val walkDataList: List<Walk> = emptyList(),

    val todayWalk: Int = 0, // ✅ 걸음 수 저장 (초기값 0)
    val totalWalkCount: String = "0",
    val totalSuccessCount: Int = 0,
    val maxContinuous: Int = 1,
    val successRate: Int = 0,
    val walkState: String = "미완료", //미완료, 대기, 완료
    val today: String = "2025-07-05",
    val calendarMonth: String = "2025-07",
    val sensor: Boolean = false

    )

sealed interface WalkSideEffect {
    class Toast(val message: String) : WalkSideEffect
}
