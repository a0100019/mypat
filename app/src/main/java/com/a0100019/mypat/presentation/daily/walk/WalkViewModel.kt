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
                    Log.d("WalkViewModel", "$steps")
                    loadData()

                    return@collectLatest
                }

                if (hasLoadedInitialData) {
                    intent {
                        reduce {
                            state.copy(todayWalk = steps - state.firstSystemWalk + state.firstSaveWalk)
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

        var systemWalk = userDao.getValue2ById(id = "walk")

        //안 들어온 시간동안 걸음 수 측정, 폰 껐다 킨거 고려
        var count = if (systemWalk.toInt() <= currentStepCount) {
                currentStepCount - systemWalk.toInt()
            } else {
                currentStepCount
            }

        var firstSaveWalk = walkUserData.value.toInt()

        if(systemWalk == "0" ) {
            // 로그인 후 첫 로그인
            if(goalCount == 0){
                userDao.update(id = "walk", value = "9999", value2 = currentStepCount.toString())
                count = 0
                firstSaveWalk = 9999
                systemWalk = currentStepCount.toString()
                postSideEffect(WalkSideEffect.Toast("첫날 미션을 위해 기본 걸음 수가 지급됩니다"))
            } else {
                userDao.update(id = "walk", value2 = currentStepCount.toString())
                count = 0
                systemWalk = currentStepCount.toString()
            }
        } else {
            userDao.update(
                id = "walk",
                value = (walkUserData.value.toInt() + count).toString(),
                value2 = currentStepCount.toString(),
                value3 = (walkUserData.value3.toInt() + count).toString()
            )
        }
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
                todayWalk = firstSaveWalk + count,
                today = currentDate,
                calendarMonth = currentDate.substring(0, 7),
                walkState = walkState,
                maxContinuous = maxStreak,
                successRate = successRatio.toInt(),
                firstSystemWalk = systemWalk.toInt(),
                firstSaveWalk = firstSaveWalk
            )
        }
    }

    fun onTodayWalkSubmitClick() = intent {

        if(state.todayWalk >= 10000){

            userDao.update(
                id = "walk",
                //두번 더해지는 거 방지
                value = (state.todayWalk - 10000 - (state.todayWalk-10000)).toString(),
            )

            //보상
            userDao.update(
                id = "money",
                value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + 1).toString()
            )

            val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            walkDao.updateSuccessByDate(date = currentDate, success = "1")

            postSideEffect(WalkSideEffect.Toast("일일 미션 완료"))

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
    val sensor: Boolean = false,
    val firstSystemWalk: Int = 0,
    val firstSaveWalk: Int = 0

    )

sealed interface WalkSideEffect {
    class Toast(val message: String) : WalkSideEffect
}
