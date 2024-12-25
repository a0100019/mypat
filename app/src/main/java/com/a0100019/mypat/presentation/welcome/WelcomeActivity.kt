package com.a0100019.mypat.presentation.welcome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.a0100019.mypat.ui.theme.MypatTheme
import dagger.hilt.android.AndroidEntryPoint

//AppCompat 이랑 ComponentActivity 차이점 ??
//엑티비티는 새로 만들면 메니페스트에 추가해야됨
@AndroidEntryPoint
class WelcomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MypatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WelcomeNavHost()
                }
            }
        }
    }
}