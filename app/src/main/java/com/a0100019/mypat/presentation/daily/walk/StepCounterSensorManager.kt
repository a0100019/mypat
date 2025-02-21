package com.a0100019.mypat.presentation.daily.walk

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun StepCounterSensorManager(
    walkViewModel: WalkViewModel
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    var stepCount by remember { mutableFloatStateOf(0f) }

    val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // 정확도 변경 처리
            }

            override fun onSensorChanged(event: SensorEvent) {
                // 걸음 수 업데이트 처리
                stepCount = event.values[0]
                walkViewModel.setStepCount(stepCount.toInt()) // 현재 걸음 수 ViewModel에 저장
            }
        }
    }

    DisposableEffect(Unit) {
        stepCounterSensor?.let {
            sensorManager.registerListener(
                sensorEventListener, it, SensorManager.SENSOR_DELAY_FASTEST
            )
        }

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }
} // End of rememberStepCounterSensorState()