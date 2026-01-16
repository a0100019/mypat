package com.a0100019.mypat.presentation.activity

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ActivityContainerScreen(
    activityViewModel: ActivityViewModel = hiltViewModel(),

    popBackStack: () -> Unit = {},
    onDailyNavigateClick: () -> Unit = {},
    onIndexNavigateClick: () -> Unit = {},
    onInformationNavigateClick: () -> Unit = {},
    onStoreNavigateClick: () -> Unit = {},

    ) {

    val activityState : ActivityState = activityViewModel.collectAsState().value

    val context = LocalContext.current

    activityViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ActivitySideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    ActivityScreen(
        situation = activityState.situation,

        onClose = activityViewModel::onClose,
        popBackStack = popBackStack,
        onDailyNavigateClick = onDailyNavigateClick,
        onIndexNavigateClick = onIndexNavigateClick,
        onInformationNavigateClick = onInformationNavigateClick,
        onStoreNavigateClick = onStoreNavigateClick
    )
}

@Composable
fun ActivityScreen(
    situation: String = "",

    onClose : () -> Unit = {},
    popBackStack: () -> Unit = {},
    onDailyNavigateClick: () -> Unit = {},
    onIndexNavigateClick: () -> Unit = {},
    onInformationNavigateClick: () -> Unit = {},
    onStoreNavigateClick: () -> Unit = {},

) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Box(
            modifier = Modifier
                .fillMaxSize()
//                .background(Color(0xFFFDFCF0)) // 마을 느낌의 따뜻한 미색 배경
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 12.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                contentAlignment = Alignment.Center
            ) {
                // 가운데 텍스트
                Text(
                    text = "마을 관리",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                )

                JustImage(
                    filePath = "etc/exit.png",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(30.dp)
                        .clickable {
                            popBackStack()
                        }
                )

            }

            // 2. 중앙 4사분면 메뉴 레이아웃
            Column(
                modifier = Modifier
                    .fillMaxSize()
                        ,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 첫 번째 줄
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 일일미션
                    Surface( // Button 대신 Surface를 사용하여 더 깔끔한 커스텀 레이아웃 구현
                        onClick = onDailyNavigateClick,
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                        shape = RoundedCornerShape(28.dp),
                        color = Color(0xFFFFF4E6), // 연주황
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(modifier = Modifier.size(56.dp).background(Color.White.copy(0.4f), CircleShape), contentAlignment = Alignment.Center) {
                                Text("📅", fontSize = 32.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("일일미션", fontWeight = FontWeight.ExtraBold, color = Color(0xFFE65100))
                        }
                    }

                    // 도감
                    Surface(
                        onClick = onIndexNavigateClick,
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                        shape = RoundedCornerShape(28.dp),
                        color = Color(0xFFE8F5E9), // 연초록
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(modifier = Modifier.size(56.dp).background(Color.White.copy(0.4f), CircleShape), contentAlignment = Alignment.Center) {
                                Text("📖", fontSize = 32.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("도감", fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 두 번째 줄
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 내정보
                    Surface(
                        onClick = onInformationNavigateClick,
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                        shape = RoundedCornerShape(28.dp),
                        color = Color(0xFFE3F2FD), // 연파랑
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(modifier = Modifier.size(56.dp).background(Color.White.copy(0.4f), CircleShape), contentAlignment = Alignment.Center) {
                                Text("👤", fontSize = 32.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("내정보", fontWeight = FontWeight.ExtraBold, color = Color(0xFF1565C0))
                        }
                    }

                    // 상점
                    Surface(
                        onClick = onStoreNavigateClick,
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                        shape = RoundedCornerShape(28.dp),
                        color = Color(0xFFFCE4EC), // 연분홍
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(modifier = Modifier.size(56.dp).background(Color.White.copy(0.4f), CircleShape), contentAlignment = Alignment.Center) {
                                Text("🛒", fontSize = 32.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("상점", fontWeight = FontWeight.ExtraBold, color = Color(0xFFC2185B))
                        }
                    }
                }
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun ActivityScreenPreview() {
    MypatTheme {
        ActivityScreen(
            situation = ""
        )
    }
}