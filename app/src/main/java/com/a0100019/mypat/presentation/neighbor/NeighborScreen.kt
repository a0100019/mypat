package com.a0100019.mypat.presentation.neighbor

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.main.management.loading.LoadingSideEffect
import com.a0100019.mypat.presentation.main.management.loading.LoadingState
import com.a0100019.mypat.presentation.main.management.loading.LoadingViewModel
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun NeighborScreen(
    neighborViewModel: NeighborViewModel = hiltViewModel(),

    popBackStack: () -> Unit = {},
    onChatNavigateClick: () -> Unit = {},
    onCommunityNavigateClick: () -> Unit = {},
    onBoardNavigateClick: () -> Unit = {},
    onPrivateRoomNavigateClick: () -> Unit = {},
    onMainNavigateClick: () -> Unit = {},
) {

    val neighborState : NeighborState = neighborViewModel.collectAsState().value

    val context = LocalContext.current

    neighborViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is NeighborSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    NeighborScreen(
        onClose = neighborViewModel::onClose,

        popBackStack = popBackStack,
        onChatNavigateClick = onChatNavigateClick,
        onCommunityNavigateClick = onCommunityNavigateClick,
        onBoardNavigateClick = onBoardNavigateClick,
        onPrivateRoomNavigateClick = onPrivateRoomNavigateClick,
        onMainNavigateClick = onMainNavigateClick,

    )
}

@Composable
fun NeighborScreen(
    text: String = "",

    onClose : () -> Unit = {},

    popBackStack: () -> Unit = {},
    onCommunityNavigateClick: () -> Unit = {},
    onChatNavigateClick: () -> Unit = {},
    onBoardNavigateClick: () -> Unit = {},
    onPrivateRoomNavigateClick: () -> Unit = {},
    onMainNavigateClick: () -> Unit = {},

    ) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 12.dp)
            ,
            verticalArrangement = Arrangement.SpaceBetween
        ){

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                contentAlignment = Alignment.Center
            ) {
                // 가운데 텍스트
                Text(
                    text = "커뮤니티",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                )

                JustImage(
                    filePath = "etc/exit.png",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(30.dp)
                        .clickable {
                            onMainNavigateClick()
                        }
                )

            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
// 그라데이션 및 디자인 요소 추가 버전
                // 자유게시판 (사진 기능 추가 강조 버전)
                val interaction2 = remember { MutableInteractionSource() }
                val isPressed2 by interaction2.collectIsPressedAsState()
                val scale2 by animateFloatAsState(if (isPressed2) 0.96f else 1f, label = "")

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp) // 정보를 더 담기 위해 높이를 살짝 키움
                        .graphicsLayer {
                            scaleX = scale2
                            scaleY = scale2
                        }
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(32.dp),
                            ambientColor = Color(0xFF4CAF50),
                            spotColor = Color(0xFF4CAF50)
                        )
                        .clickable(
                            interactionSource = interaction2,
                            indication = null,
                            onClick = onBoardNavigateClick
                        ),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFB9F6CA))
                                )
                            )
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 아이콘 영역 (핀 이모지 + 우측 하단 작은 카메라 배지로 업데이트 암시)
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("📌", fontSize = 36.sp)
                                }
                                // ⭐ 신규 기능 표시 (작은 카메라 아이콘 배지)
                                Box(
                                    modifier = Modifier
                                        .offset(x = 4.dp, y = 4.dp)
                                        .size(28.dp)
                                        .background(Color(0xFF4CAF50), CircleShape)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("📸", fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "자유게시판",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF1B5E20),
                                        letterSpacing = (-0.5).sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))

                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "이제 사진과 함께 일상을 나눠보세요!", // ⭐ 문구 변경
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "이웃들과 나누는 따뜻한 이야기",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF2E7D32).copy(alpha = 0.6f)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32).copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // --- 2. 하단: 나머지 3개 버튼 (가로로 나란히) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // 간격을 조금 좁힘
                ) {
                    // --- 전체 채팅 ---
                    MenuButton(
                        icon = "💬",
                        title = "전체 채팅",
                        subTitle = "자유로운 대화",
                        color = Color(0xFFE3F2FD),
                        textColor = Color(0xFF1565C0),
                        borderColor = Color(0xFF2196F3),
                        onClick = onChatNavigateClick,
                        modifier = Modifier.weight(1f)
                    )

                    // --- 이웃 마을 ---
                    MenuButton(
                        icon = "🏡",
                        title = "이웃 마을",
                        subTitle = "마을 둘러보기",
                        color = Color(0xFFFFF3E0),
                        textColor = Color(0xFFE65100),
                        borderColor = Color(0xFFFF9800),
                        onClick = onCommunityNavigateClick,
                        modifier = Modifier.weight(1f)
                    )

                    // --- 개인 채팅 (친구) ---
                    MenuButton(
                        icon = "✉️",
                        title = "친구",
                        subTitle = "1:1 채팅",
                        color = Color(0xFFFCE4EC),
                        textColor = Color(0xFFC2185B),
                        borderColor = Color(0xFFE91E63),
                        onClick = onPrivateRoomNavigateClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text(
                text = "하루마을 커뮤니티는 힐링과 평화로운 분위기를 지향합니다.",
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

        }

    }
}

@Composable
fun MenuButton(
    icon: String,
    title: String,
    subTitle: String,
    color: Color,
    textColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "")

    Surface(
        modifier = modifier
            .aspectRatio(0.8f) // 3개일 때는 세로로 약간 긴 것이 보기 좋음
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = color,
        border = BorderStroke(2.dp, borderColor.copy(0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(subTitle, fontSize = 10.sp, color = textColor.copy(0.7f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NeighborScreenPreview() {
    MypatTheme {
        NeighborScreen(
            text = ""
        )
    }
}