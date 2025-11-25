package com.a0100019.mypat.presentation.daily.walk

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val userDao: UserDao,
    private val walkDao: WalkDao,
    @ApplicationContext private val context: Context
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

    init {
        loadData()
    }

    private fun loadData() = intent {
        Log.d("WalkViewModel", "loadData 호출")
        val userDataList = userDao.getAllUserData()
        val walkDataList = walkDao.getAllWalkData()

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val prefs = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)

        //저장 걸음 수
        val saveSteps = prefs.getInt("saveSteps", 0)

        reduce {
            state.copy(
                userDataList = userDataList,
                walkDataList = walkDataList,
                totalWalkCount = totalWalkCount,
                totalSuccessCount = goalCount,
                saveSteps = saveSteps,
                today = today,
                calendarMonth = today.substring(0, 7),
                walkState = walkState,
                maxContinuous = maxStreak,
                successRate = successRatio.toInt(),
            )
        }
    }

    fun onTodayWalkSubmitClick() = intent {

        if(state.saveSteps >= 10000){

            userDao.update(
                id = "walk",
                //두번 더해지는 거 방지
                value = (state.saveSteps - 10000).toString(),
            )

            //보상
            userDao.update(
                id = "money",
                value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + 1).toString()
            )

            val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            walkDao.updateSuccessByDate(date = currentDate, success = "1")

            postSideEffect(WalkSideEffect.Toast("일일 미션 완료"))

            reduce {
                state.copy(
                    saveSteps = state.saveSteps - 10000,
                    walkState = "완료",
                    lastWalkCount = state.saveSteps - 10000,
                )
            }

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

    val saveSteps: Int = 0, // ✅ 걸음 수 저장 (초기값 0)
    val totalWalkCount: String = "0",
    val totalSuccessCount: Int = 0,
    val maxContinuous: Int = 1,
    val successRate: Int = 0,
    val walkState: String = "미완료", //미완료, 대기, 완료
    val today: String = "2025-07-05",
    val calendarMonth: String = "2025-07",
    val sensor: Boolean = false,
    val firstSystemWalk: Int = 0,
    val firstSaveWalk: Int = 0,
    val lastWalkCount: Int = 0,

    )

sealed interface WalkSideEffect {
    class Toast(val message: String) : WalkSideEffect
}
