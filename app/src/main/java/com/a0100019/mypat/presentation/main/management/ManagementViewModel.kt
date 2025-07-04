package com.a0100019.mypat.presentation.main.management

import android.content.Context
import androidx.lifecycle.ViewModel
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

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        walkUpdate()
        todayAttendance()
    }

    private fun todayAttendance() = intent {

        val lastData = diaryDao.getLatestDiary()
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        if(lastData.date != currentDate){

            val closeKoreanIdiomData = koreanIdiomDao.getCloseKoreanIdiom()
            if (closeKoreanIdiomData.isNotNull()) {
                closeKoreanIdiomData!!.date = currentDate
                closeKoreanIdiomData.state = "대기"
                koreanIdiomDao.update(closeKoreanIdiomData)
            }

            val closeEnglishData = englishDao.getCloseEnglish()
            if (closeEnglishData.isNotNull()) {
                closeEnglishData!!.date = currentDate
                closeEnglishData.state = "대기"
                englishDao.update(closeEnglishData)
            }

            diaryDao.insert(Diary(date = currentDate))

            //일일 알림 다이얼로그 띄우기
        }

    }

    //room에서 데이터 가져옴
    private fun walkUpdate() = intent {
        val currentStepCount = stepCounterManager.getStepCount()

        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val yesterday =
            LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val lastData = walkDao.getLatestWalkData()

        //오늘 첫 로그인, 자동 업데이트 안된 것?
        if(lastData.date != currentDate) {

            val today = LocalDate.now()  // 오늘 날짜
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val lastDate =
                LocalDate.parse(lastData.date, formatter)  // lastData.date를 LocalDate로 변환
            val daysDifference = ChronoUnit.DAYS.between(lastDate, today) + 1

            val count = if (lastData.steps < currentStepCount) {
                currentStepCount - lastData.steps
            } else {
                currentStepCount
            }

            userDao.update(id = "date", value = currentDate) // ✅ DAO는 suspend 함수이므로 안전

            walkDao.updateCountByDate(
                date = lastData.date,
                newCount = lastData.count + (count / daysDifference.toInt())
            )

            walkDao.insert(Walk(date = currentDate, count = count / daysDifference.toInt(), steps = currentStepCount))


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