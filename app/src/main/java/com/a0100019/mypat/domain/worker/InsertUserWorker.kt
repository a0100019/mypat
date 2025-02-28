package com.a0100019.mypat.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.data.room.walk.WalkDao
import com.a0100019.mypat.presentation.daily.walk.StepCounterManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltWorker
class InsertUserWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userDao: UserDao,
    private val walkDao: WalkDao,
    private val stepCounterManager: StepCounterManager

) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val stepCount = sharedPreferences.getInt("steps", 0) // SharedPreferences에서 걸음 수 가져오기

        val currentStepCount = stepCounterManager.getStepCount()


        val currentDate = LocalDate.now()  // 현재 날짜
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")  // 원하는 형식
        val formattedDate = currentDate.format(formatter)  // 날짜를 문자열로 포맷

        val job = CoroutineScope(Dispatchers.IO).launch {

            try {
                userDao.update(id = "date", value = formattedDate) // 백그라운드에서 실행
                walkDao.insert(Walk(date = formattedDate, count = currentStepCount))
            } catch (e: Exception) {
                Result.retry()  // 실패하면 재시도
            }
        }

        job.invokeOnCompletion { exception ->
            if (exception != null) {
                Result.retry()
            }
        }

        return Result.success()
    }
}

