package com.a0100019.mypat.presentation.daily.diary

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.presentation.daily.walk.WalkSideEffect
import com.a0100019.mypat.presentation.game.secondGame.SecondGameSideEffect
import com.a0100019.mypat.presentation.main.MainSideEffect
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val userDao: UserDao,
    private val diaryDao: DiaryDao,
    @ApplicationContext private val context: Context,
) : ViewModel(), ContainerHost<DiaryState, DiarySideEffect> {

    override val container: Container<DiaryState, DiarySideEffect> = container(
        initialState = DiaryState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(DiarySideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    init {

        loadData()
    }

    fun loadData() = intent {
        // 1. suspend로 바로 가져오는 유저 정보
        val userDataList = userDao.getAllUserData()
        val allDiaryData = diaryDao.getAllDiaryData()
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        if(allDiaryData.count { it.emotion == "emotion/love.png" } >= 10) {
            //매달, medal, 칭호5
            val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

            val myMedalList: MutableList<Int> =
                myMedal
                    .split("/")
                    .mapNotNull { it.toIntOrNull() }
                    .toMutableList()

            // 🔥 여기 숫자 두개 바꾸면 됨
            if (!myMedalList.contains(5)) {
                myMedalList.add(5)

                // 다시 문자열로 합치기
                val updatedMedal = myMedalList.joinToString("/")

                // DB 업데이트
                userDao.update(
                    id = "etc",
                    value3 = updatedMedal
                )
                postSideEffect(DiarySideEffect.Toast("칭호를 획득했습니다!"))
            }
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        // 날짜 파싱 + 정렬
        val dateList = allDiaryData
            .map { LocalDate.parse(it.date, formatter) }
            .sorted()
        var maxStreak = 0
        var currentStreak = 0
        for (i in dateList.indices) {
            if (i == 0 || dateList[i] == dateList[i - 1].plusDays(1)) {
                currentStreak++
            } else {
                currentStreak = 1
            }
            maxStreak = maxOf(maxStreak, currentStreak)
        }
        // 🎯 결과
        if (maxStreak >= 10) {
            // 최장 연속 출석 10일 이상
            //매달, medal, 칭호8
            val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

            val myMedalList: MutableList<Int> =
                myMedal
                    .split("/")
                    .mapNotNull { it.toIntOrNull() }
                    .toMutableList()

            // 🔥 여기 숫자 두개 바꾸면 됨
            if (!myMedalList.contains(8)) {
                myMedalList.add(8)

                // 다시 문자열로 합치기
                val updatedMedal = myMedalList.joinToString("/")

                // DB 업데이트
                userDao.update(
                    id = "etc",
                    value3 = updatedMedal
                )

                postSideEffect(DiarySideEffect.Toast("칭호를 획득했습니다!"))
            }
        }

        reduce {
            state.copy(
                userDataList = userDataList,
            )
        }

//        // 2. Flow인 일기 데이터는 collect로 가져와야 실시간 반영됨
        viewModelScope.launch {
            diaryDao.getAllFlowDiaryData().collect { diaryList ->
                reduce {
                    state.copy(
                        diaryDataList = diaryList,
                        diaryFilterDataList = diaryList,
                        dialogState = "",
                        clickDiaryData = null,
                        today = currentDate,
                        calendarMonth = currentDate.substring(0, 7),
                    )
                }
            }
        }
    }

    fun onDiaryClick(diaryData : Diary) = intent {

        if(diaryData.state == "대기") {
            userDao.update(id = "etc2", value = diaryData.date)
            postSideEffect(DiarySideEffect.NavigateToDiaryWriteScreen)
        } else {
            userDao.update(id = "etc2", value = diaryData.date)
            reduce {
                state.copy(
                    clickDiaryData = diaryData,
                )
            }
        }

    }

    fun onDiaryChangeClick() = intent {

        postSideEffect(DiarySideEffect.NavigateToDiaryWriteScreen)

    }

    fun onCloseClick() = intent {
        reduce {
            state.copy(
                clickDiaryData = null,
                dialogState = "",
            )
        }
    }

    fun onCalendarDiaryCloseClick() = intent {
        reduce {
            state.copy(
                clickDiaryData = null,
            )
        }
    }

    //검색
    fun onSearchClick() = intent {
        val newDiaryDataList = state.diaryDataList.filter { it.contents.contains(state.searchText) }
        if(state.emotionFilter != "") {
            newDiaryDataList.filter { it.emotion == state.emotionFilter }
        }
        reduce {
            state.copy(
                diaryFilterDataList = newDiaryDataList,
                dialogState = ""
            )
        }
    }

    fun onSearchClearClick() = intent {
        var newDiaryDataList = state.diaryDataList
        if(state.emotionFilter != "emotion/allEmotion.png") {
            newDiaryDataList = newDiaryDataList.filter { it.emotion == state.emotionFilter }
        }
        reduce {
            state.copy(
                diaryFilterDataList = newDiaryDataList,
                dialogState = "",
                searchText = ""
            )
        }
    }

    fun onDialogStateChange(string: String) = intent {
        reduce {
            state.copy(dialogState = string)
        }
    }


    fun onEmotionFilterClick(emotion: String) = intent {
        var newDiaryDataList = state.diaryDataList.filter { it.contents.contains(state.searchText) }
        if(emotion != "emotion/allEmotion.png") {
            newDiaryDataList = newDiaryDataList.filter { it.emotion == emotion }
        }
        reduce {
            state.copy(
                diaryFilterDataList = newDiaryDataList,
                dialogState = "",
                emotionFilter = emotion
            )
        }
    }

    //입력 가능하게 하는 코드
    @OptIn(OrbitExperimental::class)
    fun onSearchTextChange(searchText: String) = blockingIntent {
        reduce {
            state.copy(searchText = searchText)
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

    fun onDiaryDateClick(date: String) = intent {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val selectedDate = LocalDate.parse(date, formatter)
        val today = LocalDate.now()

        if (selectedDate.isAfter(today)) {
            postSideEffect(DiarySideEffect.Toast("지난 날짜를 선택해주세요"))
        } else {

            val allDiaryDataList = state.diaryDataList
            val diaryData = allDiaryDataList.find { it.date == date }

            if (diaryData == null) {
                // ✅ 해당 날짜의 일기 데이터가 존재하지 않을 때
                Log.w("Diary", "일기 데이터가 존재하지 않음: $date")

                // 예: 새 일기 작성 화면으로 이동
                userDao.update(id = "etc2", value = "0$date")
                postSideEffect(DiarySideEffect.NavigateToDiaryWriteScreen)
            }
            else if (diaryData.state == "대기") {
                // ✅ 일기 상태가 '대기'인 경우
                userDao.update(id = "etc2", value = diaryData.date)
                postSideEffect(DiarySideEffect.NavigateToDiaryWriteScreen)
            }
            else {
                // ✅ 기존 일기가 존재하는 경우
                userDao.update(id = "etc2", value = diaryData.date)
                reduce {
                    state.copy(
                        clickDiaryData = diaryData,
                    )
                }
            }

        }
    }

    fun onExitClick() = intent {
        postSideEffect(DiarySideEffect.ExitApp)
    }

    fun onDiaryAlarmChangeClick(timeString: String) = intent {
        postSideEffect(
            DiarySideEffect.CheckNotificationPermission(timeString)
        )
    }

    // 알람을 해제하려 할 때
    fun onCancelAlarmClick() = intent {

        // 1. 시스템 알람 및 저장 데이터 삭제
        cancelDiaryAlarm(context)

        // 2. State 업데이트 (필요한 경우)
        reduce {
            state.copy(
                // 예를 들어 alarmTime이라는 상태가 있다면 null로 변경
                // alarmTime = null
            )
        }

        postSideEffect(DiarySideEffect.Toast("알림이 해제되었습니다."))
        reduce {
            state.copy(
                dialogState = ""
            )
        }
    }

}

@Immutable
data class DiaryState(
    val userDataList: List<User> = emptyList(),
    val diaryDataList: List<Diary> = emptyList(),
    val diaryFilterDataList: List<Diary> = emptyList(),

    val clickDiaryData: Diary? = null,
    val writeDiaryData: Diary = Diary(date = "", contents = "", emotion = ""),
    val writePossible: Boolean = false,
    val isError: Boolean = false,
    val searchText: String = "",
    val dialogState: String = "",
    val emotionFilter: String = "emotion/allEmotion.png",
    val firstWrite: Boolean = true,
    val writeFinish: Boolean = false,
    val today: String = "2025-07-05",
    val calendarMonth: String = "2025-07",
)

//상태와 관련없는 것
sealed interface DiarySideEffect{
    class Toast(val message:String): DiarySideEffect
    data object NavigateToDiaryWriteScreen: DiarySideEffect

    object ExitApp : DiarySideEffect   // 앱 종료용

    // 🔥 추가
    data class CheckNotificationPermission(
        val timeString: String
    ) : DiarySideEffect

}