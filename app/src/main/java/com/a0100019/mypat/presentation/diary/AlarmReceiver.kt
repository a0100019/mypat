package com.a0100019.mypat.presentation.diary

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.a0100019.mypat.R
import com.a0100019.mypat.presentation.main.management.MainActivity
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 🔹 추가: 알람 시간 데이터가 있는지 확인 (취소된 경우 중단)
        val prefs = context.getSharedPreferences("diary_alarm", Context.MODE_PRIVATE)
        val savedTime = prefs.getString("alarm_time", null)
        if (savedTime == null) return

        // 🔹 연속 일수 SharedPreferences
        val streakPrefs =
            context.getSharedPreferences("diary_prefs", Context.MODE_PRIVATE)
        val diarySequence = streakPrefs.getInt("diarySequence", 0)

        val channelId = "diary_alarm_channel"
        val notificationId = 1001 // 고정 ID 사용 (너무 큰 랜덤값 방지)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "일기 알림",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "매일 일기 작성 알림"
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val contentText =
            if (diarySequence > -1) {
                "${diarySequence + 1}일 연속 일기 작성 중!"
            } else {
                "펫들이 이웃님을 기다리고 있어요 ㅠㅠ"
            }

        // SharedPreferences에 -1 저장하기
        streakPrefs.edit().putInt("diarySequence", -1).apply()

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.pet)
            .setContentTitle("일기를 작성할 시간이에요 🐶")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)

        // 🔁 다음 날 재예약
        val hour = intent.getIntExtra("hour", 21)
        val minute = intent.getIntExtra("minute", 0)

        val calendar = Calendar.getInstance().apply {
            add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val nextIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("hour", hour)
            putExtra("minute", minute)
        }

        val nextPendingIntent = PendingIntent.getBroadcast(
            context,
            888, // ✅ 등록/취소와 동일한 고유 ID 사용
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            nextPendingIntent
        )
    }
}

// 🔹 알람 예약 함수
fun scheduleDiaryAlarm(context: Context, timeString: String) {
    val prefs = context.getSharedPreferences("diary_alarm", Context.MODE_PRIVATE)
    prefs.edit().putString("alarm_time", timeString).apply()

    val (hour, minute) = timeString.split(":").map { it.toInt() }

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        if (before(Calendar.getInstance())) {
            add(Calendar.DATE, 1)
        }
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("hour", hour)
        putExtra("minute", minute)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        888, // ✅ 고유 ID 고정
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.setAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}

// 🔹 알람 취소 함수
fun cancelDiaryAlarm(context: Context) {
    val prefs = context.getSharedPreferences("diary_alarm", Context.MODE_PRIVATE)
    prefs.edit().remove("alarm_time").apply()

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        888, // ✅ 등록할 때와 동일한 ID 사용
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.cancel(pendingIntent)
    pendingIntent.cancel()
}