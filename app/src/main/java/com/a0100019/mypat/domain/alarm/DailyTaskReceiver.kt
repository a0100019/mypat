package com.a0100019.mypat.domain.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.a0100019.mypat.data.room.user.UserDao
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.BroadcastReceiverComponentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DailyTaskReceiver : BroadcastReceiver() {

    @Inject
    lateinit var userDao: UserDao // ✅ Hilt로 `userDao` 자동 주입

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("DailyTaskReceiver", "✅ 03:00에 특정 코드 실행됨!")

        CoroutineScope(Dispatchers.IO).launch {
            userDao.update(id="date", value = "11")
        }

        Log.d("DailyTaskReceiver", "✅ 하루 변경됨, 걸음 수 초기화 완료")
    }
}
