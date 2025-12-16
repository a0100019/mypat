package com.a0100019.mypat.presentation.neighbor.board

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.neighbor.chat.ChatMessage
import com.a0100019.mypat.presentation.privateChat.PrivateRoomSideEffect
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun BoardScreen(
    boardViewModel: BoardViewModel = hiltViewModel(),
    onNavigateToBoardMessageScreen: () -> Unit = {},

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
        loadBoardMessages = boardViewModel::loadBoardMessages
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
    loadBoardMessages: () -> Unit = {}

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                MainButton(
                    onClick = popBackStack,
                    text = "닫기"
                )
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFF4F4F4),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onBoardMessageClick(message.timestamp.toString())
                            }
                            .padding(12.dp)
                    ) {

                        // 상단: 이름 + 타입
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                text = message.type,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 메시지 본문
                        Text(
                            text = message.message,
                            style = MaterialTheme.typography.bodyMedium
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