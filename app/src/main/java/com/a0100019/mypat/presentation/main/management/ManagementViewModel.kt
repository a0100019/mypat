package com.a0100019.mypat.presentation.main.management

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.presentation.daily.walk.StepCounterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val userDao: UserDao,
    private val walkDao: WalkDao,
    private val koreanIdiomDao: KoreanIdiomDao,
    private val englishDao: EnglishDao,
    private val diaryDao: DiaryDao,
    private val stepCounterManager: StepCounterManager,
    @ApplicationContext private val context: Context

) : ViewModel(), ContainerHost<ManagementState, ManagementSideEffect> {

    override val container: Container<ManagementState, ManagementSideEffect> = container(
        initialState = ManagementState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(ManagementSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    init {
        todayAttendance()
    }

    private fun todayAttendance() = intent {
        val lastData = userDao.getValueById("date")
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        if (lastData != currentDate) {

            val userData = userDao.getAllUserData()

            userDao.update(id = "date", value = currentDate)
            userDao.update(id = "date", value2 = ( userData.find { it.id == "date" }!!.value2.toInt() + 1 ).toString())

            val closeKoreanIdiomData = koreanIdiomDao.getCloseKoreanIdiom()
            if (closeKoreanIdiomData != null) {
                closeKoreanIdiomData.date = currentDate
                closeKoreanIdiomData.state = "대기"
                koreanIdiomDao.update(closeKoreanIdiomData)
            }

            val closeEnglishData = englishDao.getCloseEnglish()
            if (closeEnglishData != null) {
                closeEnglishData.date = currentDate
                closeEnglishData.state = "대기"
                englishDao.update(closeEnglishData)
            }

            diaryDao.insert(Diary(date = currentDate))

            // ✅ Walk 자동 채우기 부분
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val latestWalkDate = LocalDate.parse(walkDao.getLatestWalkData().date, formatter)
            val today = LocalDate.now()

            var dateToInsert = latestWalkDate.plusDays(1)
            while (!dateToInsert.isAfter(today)) {
                walkDao.insert(Walk(date = dateToInsert.format(formatter)))
                dateToInsert = dateToInsert.plusDays(1)
            }

            // TODO: 일일 알림 다이얼로그 띄우기
        }
    }

}

@Immutable
data class ManagementState(
    val id:String = "",
    val password:String = "",
    val userData: List<User> = emptyList()
)

//상태와 관련없는 것
sealed interface ManagementSideEffect{
    class Toast(val message:String): ManagementSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}