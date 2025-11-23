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
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: android.hardware.SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {

            val totalSteps = event.values[0].toInt()

            if (startSteps == -1) {
                // 오늘 처음 측정한 누적값 저장
                startSteps = totalSteps
            }

            todaySteps = totalSteps - startSteps

            updateNotification("오늘 걸음 수: $todaySteps 걸음")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

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
            .setContentTitle("만보기 실행 중")
            .setContentText(text)
            .setSmallIcon(R.drawable.heart) // 직접 아이콘 하나 넣어줘!
            .setOngoing(true)
            .build()
    }

    @SuppressLint("NotificationPermission")
    private fun updateNotification(text: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("만보기 실행 중")
            .setContentText(text)
            .setSmallIcon(R.drawable.heart)
            .setOngoing(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }
}