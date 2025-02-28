package com.a0100019.mypat.domain.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.user.UserDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class InsertUserWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userDao: UserDao
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            // 원하는 데이터 삽입
            val user = User("")
//            userDao.update(user)
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()  // 실패하면 재시도
        }
    }
}
