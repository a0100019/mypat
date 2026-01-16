package com.a0100019.mypat.presentation.neighbor

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        onPrivateRoomNavigateClick = onPrivateRoomNavigateClick

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
                            popBackStack()
                        }
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 첫 번째 줄: 채팅 & 게시판
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- 채팅 버튼 ---
                    val interaction1 = remember { MutableInteractionSource() }
                    val isPressed1 by interaction1.collectIsPressedAsState()
                    val scale1 by animateFloatAsState(if (isPressed1) 0.95f else 1f, label = "")

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .graphicsLayer { scaleX = scale1; scaleY = scale1 }
                            .clickable(interactionSource = interaction1, indication = null, onClick = onChatNavigateClick),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFE3F2FD), // 연파랑
                        border = BorderStroke(2.dp, Color(0xFF2196F3).copy(0.2f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("💬", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("전체 채팅", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                            Text("이웃과 대화", fontSize = 12.sp, color = Color(0xFF1565C0).copy(0.7f))
                        }
                    }

                    // --- 게시판 버튼 ---
                    val interaction2 = remember { MutableInteractionSource() }
                    val isPressed2 by interaction2.collectIsPressedAsState()
                    val scale2 by animateFloatAsState(if (isPressed2) 0.95f else 1f, label = "")

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .graphicsLayer { scaleX = scale2; scaleY = scale2 }
                            .clickable(interactionSource = interaction2, indication = null, onClick = onBoardNavigateClick),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFE8F5E9), // 연초록
                        border = BorderStroke(2.dp, Color(0xFF4CAF50).copy(0.2f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("📌", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("자유게시판", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Text("공감과 응원", fontSize = 12.sp, color = Color(0xFF2E7D32).copy(0.7f))
                        }
                    }
                }

                // 두 번째 줄: 이웃 마을 & 개인 채팅
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- 이웃 마을 버튼 ---
                    val interaction3 = remember { MutableInteractionSource() }
                    val isPressed3 by interaction3.collectIsPressedAsState()
                    val scale3 by animateFloatAsState(if (isPressed3) 0.95f else 1f, label = "")

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .graphicsLayer { scaleX = scale3; scaleY = scale3 }
                            .clickable(interactionSource = interaction3, indication = null, onClick = onCommunityNavigateClick),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFFFF3E0), // 연주황
                        border = BorderStroke(2.dp, Color(0xFFFF9800).copy(0.2f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("🏡", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("이웃 마을", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            Text("마을 구경하기", fontSize = 12.sp, color = Color(0xFFE65100).copy(0.7f))
                        }
                    }

                    // --- 개인 채팅 버튼 ---
                    val interaction4 = remember { MutableInteractionSource() }
                    val isPressed4 by interaction4.collectIsPressedAsState()
                    val scale4 by animateFloatAsState(if (isPressed4) 0.95f else 1f, label = "")

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .graphicsLayer { scaleX = scale4; scaleY = scale4 }
                            .clickable(interactionSource = interaction4, indication = null, onClick = onPrivateRoomNavigateClick),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFFCE4EC), // 연분홍
                        border = BorderStroke(2.dp, Color(0xFFE91E63).copy(0.2f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("✉️", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("개인 채팅", fontWeight = FontWeight.Bold, color = Color(0xFFC2185B))
                            Text("1:1 메시지", fontSize = 12.sp, color = Color(0xFFC2185B).copy(0.7f))
                        }
                    }
                }
            }

            Text(
                text = "하루마을 커뮤니티는 힐링과 평화로운 분위기를 소중히 여깁니다. 부정적인 표현은 삼가하고, 서로를 존중하며 따뜻함을 나눠보세요",
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

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