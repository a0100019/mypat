package com.a0100019.mypat.presentation.neighbor.board

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.presentation.neighbor.chat.getPastelColorForTag
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BoardMessageScreen(
    boardMessageViewModel: BoardMessageViewModel = hiltViewModel(),

    popBackStack: () -> Unit = {},

    ) {

    val boardMessageState : BoardMessageState = boardMessageViewModel.collectAsState().value

    val context = LocalContext.current

    boardMessageViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is BoardMessageSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    BoardMessageScreen(
        boardChat = boardMessageState.boardChat,
        boardData = boardMessageState.boardData,
        text = boardMessageState.text,
        situation = boardMessageState.situation,
        anonymous = boardMessageState.anonymous,

        onClose = boardMessageViewModel::onClose,
        popBackStack = popBackStack,
        onAnonymousChange = boardMessageViewModel::onAnonymousChange,
        onTextChange = boardMessageViewModel::onTextChange,
        onBoardChatSubmitClick = boardMessageViewModel::onBoardChatSubmitClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardMessageScreen(
    boardData: BoardMessage = BoardMessage(),
    boardChat: List<BoardChatMessage> = emptyList(),
    text: String = "",
    situation: String = "",
    anonymous: String = "0",

    onClose: () -> Unit = {},
    popBackStack: () -> Unit = {},
    onAnonymousChange: (String) -> Unit = {},
    onTextChange: (String) -> Unit = {},
    onBoardChatSubmitClick: () -> Unit = {},
) {
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

            /* ---------- 상단 닫기 버튼 ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                MainButton(
                    onClick = popBackStack,
                    text = "닫기"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------- 게시판 정보 ---------- */
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (boardData.type) {
                        "0" -> "자유 게시판"
                        "1" -> "고민 게시판"
                        else -> "게시판"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = boardData.message,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (boardData.anonymous == "1") "익명" else boardData.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {

                if(boardChat.isNotEmpty()){

                    /* ---------- 댓글 리스트 ---------- */
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(boardChat) { chat ->

                            val displayName =
                                if (chat.anonymous == "1") "익명" else chat.name

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color(0xFFF2F2F2),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = chat.message,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                    }

                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f), // 화면 전체 채우기
                        contentAlignment = Alignment.Center // 가로+세로 가운데 정렬
                    ) {
                        Text(
                            text = "첫 대화를 시작해보세요",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                        )
                    }

                }

                // 입력창 + 전송버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = text,
                        onValueChange = onTextChange,
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("메시지를 입력하세요") },
                        maxLines = 4,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,// 배경색 필요 시 조정
                            focusedIndicatorColor = Color.Transparent, // 포커스 상태 밑줄 제거
                            unfocusedIndicatorColor = Color.Transparent, // 비포커스 상태 밑줄 제거
                            disabledIndicatorColor = Color.Transparent // 비활성화 상태 밑줄 제거
                        )
                    )

                    Image(
                        painter = painterResource(id = R.drawable.forwarding),
                        contentDescription = "회전된 이미지",
                        modifier = Modifier
                            .size(40.dp)
                            .rotate(90f)
                            .padding(8.dp)
                            .clickable {
                                onBoardChatSubmitClick()
                            }
                    )

                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun BoardMessageScreenPreview() {
    MypatTheme {
        BoardMessageScreen(
            boardData = BoardMessage()
        )
    }
}