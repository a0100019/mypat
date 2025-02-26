package com.a0100019.mypat.presentation.main.management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.a0100019.mypat.domain.alarm.StepAlarmManager
import com.a0100019.mypat.presentation.daily.walk.RequestPermissionScreen
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//엔트리 포인트 까먹지 말기
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var stepAlarmManager: StepAlarmManager // ✅ Hilt 주입

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 앱 실행 시 한 번만 03:00 알람 설정
        stepAlarmManager.setDailyAlarm()

        setContent {
            MypatTheme {
                MainNavHost()
                RequestPermissionScreen()
            }
        }
    }
}


