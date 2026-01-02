package com.a0100019.mypat.presentation.main.management

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.a0100019.mypat.R
import com.a0100019.mypat.domain.AppBgmManager
import com.a0100019.mypat.presentation.daily.walk.RequestPermissionScreen
import com.a0100019.mypat.presentation.store.BillingManager
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var billingManager: BillingManager   // ✅ 추가


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 SharedPreferences에서 track 불러오기 (기본값 "aa")
        val prefs = getSharedPreferences("bgm_prefs", Context.MODE_PRIVATE)
        val bgm = prefs.getString("bgm", "area/normal.webp")
        val bgmOn = prefs.getBoolean("bgmOn", true)

        // 🔹 앱 전역 배경음악 시작 (앱 켜질 때 딱 한 번만 실행)
        AppBgmManager.init(
            context = this,
            name = bgm!!,
            loop = true,
            volume = 0.2f
        )

        if (!bgmOn) AppBgmManager.pause()

        setContent {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp
            val screenHeight = configuration.screenHeightDp

            val aspectRatio = screenWidth.toFloat() / screenHeight.toFloat()
            val minRatio = 9f / 22f
            val maxRatio = 9f / 17f

            MypatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black)
                        )

                        val contentModifier = when {
                            aspectRatio < minRatio ->
                                Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(minRatio)
                                    .border(2.dp, Color.Red)
                                    .shadow(8.dp, RectangleShape, clip = false)

                            aspectRatio > maxRatio ->
                                Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(maxRatio)
                                    .border(1.dp, Color.Black)
                                    .shadow(8.dp, RectangleShape, clip = false)

                            else -> Modifier.fillMaxSize()
                        }

                        Box(modifier = contentModifier) {
                            // ✅ 여기서 단 한 번만 호출
                            MainNavHost(
                                billingManager = billingManager
                            )
                        }
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()

        val prefs = getSharedPreferences("bgm_prefs", Context.MODE_PRIVATE)
        val bgmOn = prefs.getBoolean("bgmOn", true)
        if(bgmOn){ AppBgmManager.play() }
    }

    override fun onStop() {
        super.onStop()
        AppBgmManager.pause()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
