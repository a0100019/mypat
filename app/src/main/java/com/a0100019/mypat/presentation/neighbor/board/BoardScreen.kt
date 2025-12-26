package com.a0100019.mypat.presentation.neighbor.board

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun BoardScreen(
    boardViewModel: BoardViewModel = hiltViewModel(),
    onNavigateToBoardMessageScreen: () -> Unit = {},
    onNavigateToMainScreen: () -> Unit = {},

    popBackStack: () -> Unit = {},

    ) {

    val boardState : BoardState = boardViewModel.collectAsState().value

    val context = LocalContext.current

    boardViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is BoardSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            BoardSideEffect.NavigateToBoardMessageScreen -> onNavigateToBoardMessageScreen()

        }
    }

    BoardScreen(
        boardMessages = boardState.boardMessages,
        myBoardMessages = boardState.myBoardMessages,
        text = boardState.text,
        boardType = boardState.boardType,
        boardAnonymous = boardState.boardAnonymous,
        situation = boardState.situation,

        onClose = boardViewModel::onClose,
        popBackStack = popBackStack,
        onBoardMessageClick = boardViewModel::onBoardMessageClick,
        onBoardTypeChange = boardViewModel::onBoardTypeChange,
        onBoardAnonymousChange = boardViewModel::onBoardAnonymousChange,
        onSituationChange = boardViewModel::onSituationChange,
        onTextChange = boardViewModel::onTextChange,
        onBoardSubmitClick = boardViewModel::onBoardSubmitClick,
        loadBoardMessages = boardViewModel::loadBoardMessages,
        onNavigateToMainScreen = onNavigateToMainScreen
    )
}

@Composable
fun BoardScreen(
    text: String = "",
    boardMessages: List<BoardMessage> = emptyList(),
    myBoardMessages: List<BoardMessage> = emptyList(),
    boardType: String = "free",
    boardAnonymous: String = "0",
    situation: String = "",

    onClose: () -> Unit = {},
    popBackStack: () -> Unit = {},
    onBoardMessageClick: (String) -> Unit = {},
    onBoardTypeChange: (String) -> Unit = {},
    onBoardAnonymousChange: (String) -> Unit = {},
    onSituationChange: (String) -> Unit = {},
    onTextChange: (String) -> Unit = {},
    onBoardSubmitClick: () -> Unit = {},
    loadBoardMessages: () -> Unit = {},
    onNavigateToMainScreen: () -> Unit = {},

) {

    when(situation) {
        "boardSubmit" -> BoardSubmitDialog(
            text = text,
            anonymous = boardAnonymous,
            type = boardType,
            onClose = onClose,
            onChangeAnonymousClick = onBoardAnonymousChange,
            onChangeTypeClick = onBoardTypeChange,
            onTextChange = onTextChange,
            onConfirmClick = onBoardSubmitClick
        )
        "boardSubmitConfirm" -> BoardSubmitConfirmDialog(
            onDismissClick = {
                onClose()
                loadBoardMessages()
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        BackGroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "게시판",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .padding(bottom = 6.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                MainButton(
                    onClick = {
                        onSituationChange("boardSubmit")
                    },
                    text = "게시글 작성하기"
                )
                MainButton(
                    onClick = {
                        if(situation == "myBoard") onSituationChange("") else onSituationChange("myBoard")
                    },
                    text = if(situation == "myBoard") "전체 게시물 보기" else "내 게시물 보기"
                )
                MainButton(
                    onClick = onNavigateToMainScreen,
                    text = "닫기"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔽 여기부터 Board 메시지 리스트
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    if(situation == "myBoard") myBoardMessages.reversed() else boardMessages.reversed()
                ) { message ->
                    val isAnonymous = message.anonymous == "1"
                    val displayName = if (isAnonymous) "익명" else message.name

                    // ⏰ 타임스탬프 텍스트 (함수 분리 없이 inline)
                    val timeText = remember(message.timestamp) {
                        val sdf = java.text.SimpleDateFormat("M/d HH:mm", java.util.Locale.getDefault())
                        sdf.format(java.util.Date(message.timestamp))
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFF8F8F8),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE6E6E6),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                onBoardMessageClick(message.timestamp.toString())
                            }
                            .padding(14.dp)
                    ) {

                        // ── 상단: 👤 작성자 · 태그 / 타입 · ⏰ 시간 ──
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // 왼쪽: 👤 이름 + 태그
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2F2F2F)
                                )

                                if (message.anonymous != "1") {
                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = "#${message.tag}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF777777)
                                    )
                                }
                            }

                            // 오른쪽: 타입 뱃지 + 시간
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                val typeEmoji = when (message.type) {
                                    "congratulation" -> "🎉"
                                    "worry" -> "💭"
                                    else -> "🌱"
                                }

                                val typeBackgroundColor = when (message.type) {
                                    "congratulation" -> Color(0xFFFFF1CC)
                                    "worry" -> Color(0xFFE6F1FB)
                                    else -> Color(0xFFEAF4EC)
                                }

                                val typeText = when (message.type) {
                                    "congratulation" -> "축하"
                                    "worry" -> "고민"
                                    else -> "자유"
                                }

                                Text(
                                    text = "💬 ${message.answerCount}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF888888)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = typeBackgroundColor,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "$typeEmoji $typeText",
                                        fontSize = 11.sp,
                                        color = Color(0xFF555555)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // ⏰ 날짜·시간
                                Text(
                                    text = timeText,
                                    fontSize = 11.sp,
                                    color = Color(0xFF999999)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // ── 메시지 본문 (4줄 제한) ──
                        Text(
                            text = message.message,
                            fontSize = 14.sp,
                            color = Color(0xFF333333),
                            lineHeight = 20.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )

                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BoardScreenPreview() {
    MypatTheme {
        BoardScreen(
            text = ""
        )
    }
}