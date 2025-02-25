package com.a0100019.mypat.domain.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyTaskReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("DailyTaskReceiver", "✅ 03:00에 특정 코드 실행됨!")

        // ✅ 실행할 코드 (예: 걸음 수 초기화)
        val sharedPreferences = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
        val currentStepCount = sharedPreferences.getInt("last_saved_steps", 0)

        sharedPreferences.edit()
            .putInt("daily_step_offset", currentStepCount)
            .putString("last_saved_date", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                Date()
            ))
            .apply()

        Log.d("DailyTaskReceiver", "✅ 하루 변경됨, 걸음 수 초기화 완료")
    }
}
