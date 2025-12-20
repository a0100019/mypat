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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.loading.LoadingSideEffect
import com.a0100019.mypat.presentation.loading.LoadingState
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
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
        onBoardNavigateClick = onBoardNavigateClick

    )
}

@Composable
fun NeighborScreen(
    text: String = "",

    onClose : () -> Unit = {},

    popBackStack: () -> Unit = {},
    onCommunityNavigateClick: () -> Unit = {},
    onChatNavigateClick: () -> Unit = {},
    onBoardNavigateClick: () -> Unit = {}

) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


        }

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
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
                    style = MaterialTheme.typography.displaySmall
                )

                // 오른쪽 버튼
                MainButton(
                    text = "닫기",
                    onClick = popBackStack,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .padding(top = 36.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp) // ← 아이템 사이 간격
            ){

                item {
                    //버튼 기본 설정
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.scrim,
                        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onChatNavigateClick
                            )
                            .padding(top = 6.dp, bottom = 6.dp)
                    ) {
                        Box {

                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "채팅",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "이웃들과 이야기를 나눠보세요",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "포근한 대화가 마을을 밝힙니다",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier,
                                )
                            }
                        }

                    }
                }

                item {
                    //버튼 기본 설정
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.scrim,
                        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onBoardNavigateClick
                            )
                            .padding(top = 6.dp, bottom = 6.dp)
                    ) {
                        Box {

                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "게시판",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "공감과 응원이 오가는 게시판이에요",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "다양한 주제로 이웃들과 이야기해보세요",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier,
                                )
                            }
                        }

                    }
                }

                item {
                    //버튼 기본 설정
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.scrim,
                        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onCommunityNavigateClick
                            )
                            .padding(top = 6.dp, bottom = 6.dp)
                    ) {
                        Box {

                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "이웃 마을",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "이웃 마을을 구경해보세요",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                    ,
                                )
                                Text(
                                    text = "하루에 한 번 업데이트 됩니다",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier,
                                )
                            }
                        }

                    }
                }

            }

            Text(
                text = "하루마을 커뮤니티는 힐링과 평화로운 분위기를 소중히 여깁니다. 부정적인 표현은 삼가하고, 서로를 존중하며 따뜻함을 나눠보세요",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(30.dp)
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