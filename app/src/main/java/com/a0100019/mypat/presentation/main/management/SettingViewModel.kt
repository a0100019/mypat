package com.a0100019.mypat.presentation.main.management

import androidx.lifecycle.ViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.presentation.daily.walk.StepCounterManager
import dagger.hilt.android.lifecycle.HiltViewModel
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
class SettingViewModel @Inject constructor(
    private val userDao: UserDao,
    private val walkDao: WalkDao,
    private val stepCounterManager: StepCounterManager
) : ViewModel(), ContainerHost<SettingState, SettingSideEffect> {

    override val container: Container<SettingState, SettingSideEffect> = container(
        initialState = SettingState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _ , throwable ->
                intent {
                    postSideEffect(SettingSideEffect.Toast(message = throwable.message.orEmpty()))
                }
            }
        }
    )

    // 뷰 모델 초기화 시 모든 user 데이터를 로드
    init {
        loadData()
    }

    //room에서 데이터 가져옴
    private fun loadData() = intent {
        val currentStepCount = stepCounterManager.getStepCount()

        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val yesterday =
            LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val lastData = walkDao.getLatestWalkData()

        if(lastData.date != currentDate) {

            if (lastData.date == yesterday) {

                val count = if (lastData.steps < currentStepCount) {
                    currentStepCount - lastData.steps
                } else {
                    currentStepCount
                }

                userDao.update(id = "date", value = currentDate) // ✅ DAO는 suspend 함수이므로 안전
                walkDao.updateCountByDate(date = lastData.date, newCount = lastData.count + count)
                walkDao.insert(Walk(date = currentDate, count = 0, steps = currentStepCount))

            } else {

                //며칠 차이가 날 때
                val today = LocalDate.now()  // 오늘 날짜
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val lastDate =
                    LocalDate.parse(lastData.date, formatter)  // lastData.date를 LocalDate로 변환
                val daysDifference = ChronoUnit.DAYS.between(lastDate, today)

                val count = if (lastData.steps < currentStepCount) {
                    currentStepCount - lastData.steps
                } else {
                    currentStepCount
                }

                userDao.update(id = "date", value = currentDate) // ✅ DAO는 suspend 함수이므로 안전
                if (daysDifference.toInt() != 0) {
                    walkDao.updateCountByDate(
                        date = lastData.date,
                        newCount = lastData.count + count / daysDifference.toInt()
                    )
                } else {
                    walkDao.updateCountByDate(
                        date = lastData.date,
                        newCount = lastData.count + count
                    )
                }
                walkDao.insert(Walk(date = currentDate, count = 0, steps = currentStepCount))

            }
        }

    }

}



@Immutable
data class SettingState(
    val id:String = "",
    val password:String = "",
    val userData: List<User> = emptyList()
)


//상태와 관련없는 것
sealed interface SettingSideEffect{
    class Toast(val message:String): SettingSideEffect
//    data object NavigateToDailyActivity: LoadingSideEffect

}