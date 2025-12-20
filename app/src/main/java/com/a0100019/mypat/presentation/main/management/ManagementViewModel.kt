package com.a0100019.mypat.presentation.main.management

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.item.ItemDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.letter.LetterDao
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.presentation.daily.walk.StepCounterManager
import com.a0100019.mypat.presentation.store.StoreSideEffect
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
    private val letterDao: LetterDao,
    private val itemDao: ItemDao,
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

            val allEnglishDataTest = englishDao.getAllEnglishData()
            var lastDate = allEnglishDataTest
                .filter { it.date != "0" }
                .maxByOrNull { it.id }
                ?.date ?: "0"

            val totalDate = userDao.getValue2ById("date")
            if(totalDate >= "100") {
                lastDate = walkDao.getLatestWalkData().date
            }

            if(lastDate != currentDate){
                val userData = userDao.getAllUserData()

                userDao.update(id = "date", value = currentDate)
                userDao.update(
                    id = "date",
                    value2 = (userData.find { it.id == "date" }!!.value2.toInt() + 1).toString()
                )

                //출석 일수 확인해서 편지 전송
                when (userData.find { it.id == "date" }!!.value2.toInt() + 1) {
                    7 -> letterDao.updateDateByTitle(title = "7일 출석 감사 편지", todayDate = currentDate)
                    30 -> letterDao.updateDateByTitle(
                        title = "30일 출석 감사 편지",
                        todayDate = currentDate
                    )

                    //매달, medal, 칭호22
                    50 -> {
                        //매달, medal, 칭호22
                        val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

                        val myMedalList: MutableList<Int> =
                            myMedal
                                .split("/")
                                .mapNotNull { it.toIntOrNull() }
                                .toMutableList()

                        // 🔥 여기 숫자 두개 바꾸면 됨
                        if (!myMedalList.contains(22)) {
                            myMedalList.add(22)

                            // 다시 문자열로 합치기
                            val updatedMedal = myMedalList.joinToString("/")

                            // DB 업데이트
                            userDao.update(
                                id = "etc",
                                value3 = updatedMedal
                            )

                            postSideEffect(ManagementSideEffect.Toast("칭호를 획득했습니다!"))
                        }

                    }


                    100 -> letterDao.updateDateByTitle(
                        title = "100일 출석 감사 편지",
                        todayDate = currentDate
                    )
                }

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

                val allDiaries = diaryDao.getAllDiaryData()

                // id < 10000 인 것들 중에서만 최대값 찾기
                val maxUnder10000 = allDiaries
                    .filter { it.id < 10000 }
                    .maxOfOrNull { it.id } ?: 0

                val newId = maxUnder10000 + 1   // 아무것도 없으면 1부터 시작

                diaryDao.insert(
                    Diary(
                        id = newId,
                        date = currentDate,            // "2025-11-12" 같은 형식
                        // emotion, state, contents 는 디폴트 쓰면 생략 가능
                    )
                )

                walkDao.insert(Walk(date = currentDate))
            }
        }



        //칭호, 편지 관리
        if(itemDao.getAllCloseItemData().isEmpty()) {
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            letterDao.updateDateByTitle(title = "모든 아이템 획득 축하 편지", todayDate = today)
        }

        //매달, medal, 칭호1
        val myMedal = userDao.getAllUserData().find { it.id == "etc" }!!.value3

        val myMedalList: MutableList<Int> =
            myMedal
                .split("/")
                .mapNotNull { it.toIntOrNull() }
                .toMutableList()

        // 🔥 여기 숫자 두개 바꾸면 됨
        if (!myMedalList.contains(1)) {
            myMedalList.add(1)

            // 다시 문자열로 합치기
            val updatedMedal = myMedalList.joinToString("/")

            // DB 업데이트
            userDao.update(
                id = "etc",
                value3 = updatedMedal
            )

            postSideEffect(ManagementSideEffect.Toast("칭호를 획득했습니다!"))
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