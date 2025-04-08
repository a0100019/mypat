package com.a0100019.mypat.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Worker
import co.yml.charts.common.extensions.isNotNull
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.diary.DiaryDao
import com.a0100019.mypat.data.room.english.EnglishDao
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiomDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.presentation.daily.walk.StepCounterManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@HiltWorker
class InsertUserWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userDao: UserDao,
    private val walkDao: WalkDao,
    private val koreanIdiomDao: KoreanIdiomDao,
    private val englishDao: EnglishDao,
    private val diaryDao: DiaryDao,
    private val stepCounterManager: StepCounterManager

) : CoroutineWorker(context, workerParams) { // ✅ CoroutineWorker 사용

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) { // ✅ 백그라운드에서 실행
            try {
                val currentStepCount = stepCounterManager.getStepCount()

                val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val yesterday =
                    LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                val lastData = walkDao.getLatestWalkData()

                if(lastData.date != currentDate) {

                    val closeKoreanIdiomData = koreanIdiomDao.getCloseKoreanIdiom()
                    if(closeKoreanIdiomData.isNotNull()) {
                        closeKoreanIdiomData!!.date = currentDate
                        closeKoreanIdiomData.state = "대기"
                        koreanIdiomDao.update(closeKoreanIdiomData)
                    }

                    val closeEnglishData = englishDao.getCloseEnglish()
                    if(closeEnglishData.isNotNull()) {
                        closeEnglishData!!.date = currentDate
                        closeEnglishData.state = "대기"
                        englishDao.update(closeEnglishData)
                    }

                    diaryDao.insert(Diary(date = currentDate))

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


                Result.success() // ✅ 성공 시 반환
            } catch (e: Exception) {
                Result.retry() // ✅ 실패 시 재시도
            }
        }
    }
}
