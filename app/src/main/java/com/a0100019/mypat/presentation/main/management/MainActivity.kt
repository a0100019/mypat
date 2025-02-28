package com.a0100019.mypat.presentation.main.management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.a0100019.mypat.domain.worker.scheduleDailyInsertUserWorker
import com.a0100019.mypat.presentation.daily.walk.RequestPermissionScreen
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 매일 특정 시간에 userDao.insert() 실행
        scheduleDailyInsertUserWorker(this)

        setContent {
            MypatTheme {
                MainNavHost()
                RequestPermissionScreen()
            }
        }
    }
}
