package com.a0100019.mypat.presentation.activity

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.format.TextStyle

@Composable
fun ActivityContainerScreen(
    activityViewModel: ActivityViewModel = hiltViewModel(),

    popBackStack: () -> Unit = {},
    onDailyNavigateClick: () -> Unit = {},
    onIndexNavigateClick: () -> Unit = {},
    onInformationNavigateClick: () -> Unit = {},
    onStoreNavigateClick: () -> Unit = {},
    onWorldNavigateClick: () -> Unit = {},

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
        onStoreNavigateClick = onStoreNavigateClick,
        onWorldNavigateClick = onWorldNavigateClick
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
    onWorldNavigateClick: () -> Unit = {},

) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Column (
            modifier = Modifier
                .fillMaxSize()
//                .background(Color(0xFFFDFCF0)) // 마을 느낌의 따뜻한 미색 배경
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 12.dp)
            ,
            verticalArrangement = Arrangement.SpaceBetween
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

            // 중앙 4사분면 메뉴 레이아웃
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 첫 번째 줄: 일일미션 & 도감
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MenuCard(
                        title = "하루 미션",
                        subTitle = "꾸준한 성장",
                        icon = "📅",
                        backgroundColor = Color(0xFFFFF4E6),
                        textColor = Color(0xFFE65100),
                        onClick = onDailyNavigateClick,
                        modifier = Modifier.weight(1f)
                    )
                    MenuCard(
                        title = "도감",
                        subTitle = "모험의 기록",
                        icon = "📖",
                        backgroundColor = Color(0xFFE8F5E9),
                        textColor = Color(0xFF2E7D32),
                        onClick = onIndexNavigateClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                // 두 번째 줄: 내정보 & 상점
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MenuCard(
                        title = "내정보",
                        subTitle = "프로필 관리",
                        icon = "👤",
                        backgroundColor = Color(0xFFE3F2FD),
                        textColor = Color(0xFF1565C0),
                        onClick = onInformationNavigateClick,
                        modifier = Modifier.weight(1f)
                    )
                    MenuCard(
                        title = "상점",
                        subTitle = "아이템 구매",
                        icon = "🛒",
                        backgroundColor = Color(0xFFFCE4EC),
                        textColor = Color(0xFFC2185B),
                        onClick = onStoreNavigateClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                // --- 마을 꾸미기 버튼 (업그레이드 버전) ---
                val interactionWorld = remember { MutableInteractionSource() }
                val isPressedWorld by interactionWorld.collectIsPressedAsState()

                // 눌렀을 때 크기 변화와 동시에 아래로 살짝 내려가는 효과 (물리 버튼 느낌)
                val scaleWorld by animateFloatAsState(if (isPressedWorld) 0.96f else 1f, label = "scale")
                val translateY by animateFloatAsState(if (isPressedWorld) 4f else 0f, label = "translateY")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp) // 그림자 공간을 위해 높이 살짝 증가
                        .graphicsLayer {
                            scaleX = scaleWorld
                            scaleY = scaleWorld
                            translationY = translateY
                        }
                        .clickable(
                            interactionSource = interactionWorld,
                            indication = null,
                            onClick = onWorldNavigateClick
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // 1. 버튼 하단 입체감 (짙은 그림자/두께감)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .offset(y = 4.dp) // 배경보다 살짝 아래 배치
                            .background(Color(0xFFB39DDB), RoundedCornerShape(20.dp))
                    )

                    // 2. 메인 버튼 본체
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF3E5F5), // 기본 파스텔 보라
                        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.5f)) // 반짝이는 외곽선
                    ) {
                        // 은은한 그라데이션 추가를 위해 Box 사용
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.3f), // 상단 하이라이트
                                            Color.Transparent
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // 아이콘에 은은한 후광 효과
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color.White.copy(alpha = 0.6f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✨", fontSize = 18.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "마을 꾸미기",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF673AB7), // 깊이감 있는 보라색
                                    style = androidx.compose.ui.text.TextStyle(
                                        shadow = Shadow(
                                            color = Color.Black.copy(alpha = 0.1f),
                                            offset = Offset(2f, 2f),
                                            blurRadius = 2f
                                        )
                                    )
                                )
                            }
                        }
                    }
                }


            }

            Text(
                text = "나만의 멋진 마을을 만들어 보아요",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )

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

@Composable
fun MenuCard(
    title: String,
    subTitle: String,
    icon: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scaleAnimation")

    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
        border = BorderStroke(2.dp, textColor.copy(0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.ExtraBold, color = textColor)
            Text(subTitle, fontSize = 12.sp, color = textColor.copy(0.7f))
        }
    }
}