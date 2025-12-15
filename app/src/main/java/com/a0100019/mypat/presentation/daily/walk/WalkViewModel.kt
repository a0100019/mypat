package com.a0100019.mypat.presentation.daily.walk

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.presentation.daily.diary.DiarySideEffect
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

        //걸음 수 기록
        val stepsRaw = prefs.getString("stepsRaw", "$today.1") ?: "$today.1"

        userDao.update(id = "etc2", value2 = stepsRaw)

        reduce {
            state.copy(
                userDataList = userDataList,
                walkDataList = walkDataList,
                stepsRaw = stepsRaw,
                saveSteps = saveSteps,
                today = today,
                calendarMonth = today.substring(0, 7),
                baseDate = today
            )
        }

        // stepsRaw → 날짜별 걸음수 Map
        val items = stepsRaw.split("/").filter { it.isNotBlank() }
        val walkMap = items
            .mapNotNull {
                val parts = it.split(".")
                if (parts.size == 2) parts[0] to parts[1].toInt() else null
            }
            .toMap()
        // 전체 걸음 수
        val totalSteps = walkMap.values.sum()
        if(totalSteps * 0.65 / 1000.0 >= 325.0) {
            //매달, medal, 칭호9
            val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

            val myMedalList: MutableList<Int> =
                myMedal
                    .split("/")
                    .mapNotNull { it.toIntOrNull() }
                    .toMutableList()

            // 🔥 여기 숫자 두개 바꾸면 됨
            if (!myMedalList.contains(9)) {
                myMedalList.add(9)

                // 다시 문자열로 합치기
                val updatedMedal = myMedalList.joinToString("/")

                // DB 업데이트
                userDao.update(
                    id = "etc",
                    value3 = updatedMedal
                )

                postSideEffect(WalkSideEffect.Toast("칭호를 획득했습니다!"))
            }
        }

    }

    fun onTodayWalkSubmitClick() = intent {

        if(state.saveSteps >= 5000){

            //보상
            userDao.update(
                id = "money",
                value = (state.userDataList.find { it.id == "money" }!!.value.toInt() + 1).toString()
            )

            postSideEffect(WalkSideEffect.Toast("햇살 +1"))

            val prefs = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)

            prefs.edit()
                .putInt("saveSteps", 0)
                .apply()

            reduce {
                state.copy(
                    saveSteps = 0,
                )
            }

        } else {
            postSideEffect(WalkSideEffect.Toast("걸음 수가 부족합니다"))
        }

    }

    fun onSituationChangeClick(situation: String) = intent {
        when(situation) {
            "month" -> reduce {
                state.copy(
                    situation = "month"
                )
            }
            "week" -> reduce {
                state.copy(
                    situation = "week"
                )
            }
            "record" -> reduce {
                state.copy(
                    situation = "record"
                )
            }
        }
    }

    fun onCalendarMonthChangeClick(direction: String) = intent {

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        if(state.situation == "month"){
            val oldMonth = state.calendarMonth // 예: "2025-04"
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
            val yearMonth = YearMonth.parse(oldMonth, formatter)

            val newYearMonth = when (direction) {
                "left" -> yearMonth.minusMonths(1)
                "right" -> yearMonth.plusMonths(1)
                else -> yearMonth
            }

            val newMonth = newYearMonth.format(formatter)
            if (direction == "today") {
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
        } else if (state.situation == "week") {

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val oldDate = LocalDate.parse(state.baseDate, formatter)

            val newDate = when (direction) {
                "left" -> oldDate.minusDays(7)
                "right" -> oldDate.plusDays(7)
                "today" -> LocalDate.parse(state.today)
                else -> oldDate
            }

            reduce {
                state.copy(
                    baseDate = newDate.format(formatter)
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
    val stepsRaw: String = "",
    val today: String = "2025-07-05",
    val calendarMonth: String = "2025-07",
    val baseDate: String = "2025-11-26",
    val situation: String = "record"

    )

sealed interface WalkSideEffect {
    class Toast(val message: String) : WalkSideEffect
}
