package com.a0100019.mypat.presentation.main.management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.a0100019.mypat.presentation.main.FullScreenWithContent
import com.a0100019.mypat.ui.theme.MypatTheme
import dagger.hilt.android.AndroidEntryPoint

//엔트리 포인트 까먹지 말기
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MypatTheme {
//                MainNavHost()
                FullScreenWithContent()
            }
        }
    }
}

