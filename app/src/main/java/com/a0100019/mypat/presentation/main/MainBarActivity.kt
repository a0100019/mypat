package com.a0100019.mypat.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.a0100019.mypat.ui.theme.MypatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainBarActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MypatTheme {
                MainScreen()
            }
        }
    }
}