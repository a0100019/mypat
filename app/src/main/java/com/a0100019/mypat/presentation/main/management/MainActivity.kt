package com.a0100019.mypat.presentation.main.management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.a0100019.mypat.presentation.daily.walk.RequestPermissionScreen
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp
            val screenHeight = configuration.screenHeightDp

            val aspectRatio = screenWidth.toFloat() / screenHeight.toFloat()
            val minRatio = 9f / 22f // 약 0.409
            val maxRatio = 9f / 15f // 약 0.6

            MypatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            aspectRatio < minRatio -> {
                                // 세로가 너무 긴 경우 → 최소 비율(9:22)에 맞춰서 보여줌
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(minRatio)
                                ) {
                                    MainNavHost()
                                }
                            }
                            aspectRatio > maxRatio -> {
                                // 가로가 너무 넓은 경우 → 최대 비율(9:15)에 맞춰서 보여줌
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(maxRatio)
                                ) {
                                    MainNavHost()
                                }
                            }
                            else -> {
                                // 정상 범위 → 꽉 채워서 보여줌
                                MainNavHost()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars()) // 상태바 + 내비게이션바 숨기기
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
