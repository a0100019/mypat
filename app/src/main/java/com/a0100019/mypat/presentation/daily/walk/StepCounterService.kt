//package com.a0100019.mypat.presentation.daily.walk
//
//import android.app.*
//import android.content.Intent
//import android.os.Build
//import android.os.IBinder
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.a0100019.mypat.presentation.main.management.MainActivity
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.flow.MutableStateFlow
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class StepCounterService : Service(), SensorEventListener {
//
//    private var sensorManager: SensorManager? = null
//    private var stepSensor: Sensor? = null
//    private var stepCount = 0
//
//    companion object {
//        val stepsFlow = MutableStateFlow(0) // ✅ ViewModel에서 구독 가능
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//
//        Log.d("StepCounterService", "✅ 서비스 onCreate() 호출됨")
//
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
//
//        if (stepSensor == null) {
//            Log.e("StepCounterService", "🚨 걸음 센서를 찾을 수 없음. 가속도 센서를 사용합니다.")
//            stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) // 대체 센서 사용
//        }
//
//        Log.d("StepCounterService", "✅ Foreground Service 시작")
//        startForeground(1, createNotification()) // ✅ Foreground Service 강제 실행
//
//        if (stepSensor != null) {
//            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
//            Log.d("StepCounterService", "✅ 센서 리스너 등록됨: ${stepSensor!!.name}")
//        } else {
//            Log.e("StepCounterService", "🚨 센서를 찾을 수 없어 서비스 종료")
//            stopSelf()
//        }
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Log.d("StepCounterService", "✅ Foreground Service onStartCommand 실행됨")
//        return START_STICKY // ✅ 서비스 강제 재시작
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
//            stepCount = event.values[0].toInt()
//            stepsFlow.value = stepCount // ✅ ViewModel로 데이터 전송
//            Log.d("StepCounterService", "✅ 걸음 수 업데이트됨: $stepCount")
//        } else if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
//            Log.d("StepCounterService", "🚨 가속도 센서 감지됨 (걸음 수 센서가 없음)")
//        }
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
//
//    override fun onDestroy() {
//        super.onDestroy()
//        sensorManager?.unregisterListener(this)
//        Log.d("StepCounterService", "🚨 서비스 종료됨")
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//
//    private fun createNotification(): Notification {
//        val channelId = "step_counter_channel"
//        val channelName = "Step Counter Tracking"
//
//        val notificationManager = getSystemService(NotificationManager::class.java)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_DEFAULT // ✅ 중요도를 높여서 사용자 인식 강화
//            ).apply {
//                setShowBadge(true) // ✅ 앱 아이콘 배지 표시 가능
//                enableVibration(false) // ✅ 진동 없음
//                enableLights(false) // ✅ LED 없음
//            }
//
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notificationIntent = Intent(this, MainActivity::class.java) // ✅ 클릭 시 앱 실행
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        return NotificationCompat.Builder(this, channelId)
//            .setContentTitle("만보기 실행 중")
//            .setContentText("걸음 수를 측정하고 있습니다.")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setContentIntent(pendingIntent) // ✅ 클릭 시 앱 실행
//            .setOngoing(true) // ✅ 사용자가 직접 끌 때까지 유지
//            .setPriority(NotificationCompat.PRIORITY_HIGH) // ✅ 중요도 높이기
//            .build()
//    }
//
//}
