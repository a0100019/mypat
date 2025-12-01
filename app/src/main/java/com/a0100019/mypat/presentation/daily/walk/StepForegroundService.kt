package com.a0100019.mypat.presentation.daily.walk

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.a0100019.mypat.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepForegroundService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var startSteps = -1
    private var todaySteps = 0

    private val channelId = "step_counter_channel"

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        createNotificationChannel()
        startForeground(1, createNotification("걸음 수 측정 중..."))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)

        // 알림 제거
        val manager = getSystemService(NotificationManager::class.java)
        manager.cancel(1)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {

            // SharedPreferences 불러오기
            val prefs = getSharedPreferences("step_prefs", Context.MODE_PRIVATE)

            //저장 걸음 수
            // 기존 걸음 수 가져오기
            val saveSteps = prefs.getInt("saveSteps", 0)
            // 그냥 +1 해서 저장
            val updatedSteps = saveSteps + 1
            prefs.edit()
                .putInt("saveSteps", updatedSteps)
                .apply()

            // 오늘 날짜 (YYMMDD)
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date())

            //걸음 수 기록
            val stepsRaw = prefs.getString("stepsRaw", "$today.1") ?: "$today.1"

            // "/" 기준으로 날짜 목록 나누기
            val items = stepsRaw.split("/").toMutableList()

            var updated = false

            for (i in items.indices) {
                val parts = items[i].split(".")
                if (parts.size == 2) {
                    val date = parts[0]
                    val count = parts[1].toInt()

                    if (date == today) {
                        // 오늘 날짜 존재 → 걸음수 +1
                        val newCount = count + 1
                        items[i] = "$today.$newCount"
                        updated = true
                        // 알림에 표시
                        updateNotification("$newCount 걸음")
                        break
                    }
                }
            }

            // 오늘 날짜가 없었다면 새로 추가
            if (!updated) {
                items.add("$today.1")
                // 알림에 표시
                updateNotification("1 걸음")
            }

            // 다시 "/"로 합치기
            val newRaw = items.joinToString("/")

            prefs.edit()
                .putString("stepsRaw", newRaw)
                .apply()


        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "걸음 수 측정",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("만보기")
            .setContentText(text)
            .setSmallIcon(R.drawable.pet) // 직접 아이콘 하나 넣어줘!
            .setOngoing(true)
            .build()
    }

    @SuppressLint("NotificationPermission")
    private fun updateNotification(text: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("만보기")
            .setContentText(text)
            .setSmallIcon(R.drawable.pet)
            .setOngoing(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }
}