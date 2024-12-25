package com.a0100019.mypat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.a0100019.mypat.presentation.main.MainNavHost
import com.a0100019.mypat.presentation.main.MainScreen
import com.a0100019.mypat.presentation.welcome.SelectScreen
import com.a0100019.mypat.presentation.welcome.WelcomeNavHost
import com.a0100019.mypat.presentation.welcome.WelcomeRoute
import com.a0100019.mypat.ui.theme.MypatTheme
import dagger.hilt.android.AndroidEntryPoint

//엔트리 포인트 까먹지 말기
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MypatTheme {
                SelectScreen()
            }
        }
    }
}

