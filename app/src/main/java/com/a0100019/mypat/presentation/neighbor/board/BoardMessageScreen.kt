package com.a0100019.mypat.presentation.neighbor.board

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.photo.Photo
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.diary.DiaryPhotoDialog
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.neighbor.chat.getPastelColorForTag
import com.a0100019.mypat.presentation.neighbor.community.CommunitySideEffect
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
    onNavigateToBoardScreen: () -> Unit = {},
    onNavigateToNeighborInformationScreen: () -> Unit = {},

    ) {

    val boardMessageState : BoardMessageState = boardMessageViewModel.collectAsState().value

    val context = LocalContext.current

    boardMessageViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is BoardMessageSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            BoardMessageSideEffect.NavigateToNeighborInformationScreen -> onNavigateToNeighborInformationScreen()
        }
    }

    BoardMessageScreen(
        boardChat = boardMessageState.boardChat,
        boardData = boardMessageState.boardData,
        text = boardMessageState.text,
        situation = boardMessageState.situation,
        anonymous = boardMessageState.anonymous,
        userDataList = boardMessageState.userDataList,

        onClose = boardMessageViewModel::onClose,
        popBackStack = popBackStack,
        onAnonymousChange = boardMessageViewModel::onAnonymousChange,
        onTextChange = boardMessageViewModel::onTextChange,
        onBoardChatSubmitClick = boardMessageViewModel::onBoardChatSubmitClick,
        onSituationChange = boardMessageViewModel::onSituationChange,
        onBoardDelete = boardMessageViewModel::onBoardDelete,
        onNavigateToBoardScreen = onNavigateToBoardScreen,
        onBoardChatDelete = boardMessageViewModel::onBoardChatDelete,
        onNeighborInformationClick = boardMessageViewModel::onNeighborInformationClick,

        photoDataList = boardMessageState.photoDataList,
        isPhotoLoading = boardMessageState.isPhotoLoading,
        clickPhotoChange = boardMessageViewModel::clickPhotoChange,
        clickPhoto = boardMessageState.clickPhoto
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BoardMessageScreen(
    boardData: BoardMessage = BoardMessage(),
    boardChat: List<BoardChatMessage> = emptyList(),
    userDataList: List<User> = emptyList(),
    text: String = "",
    situation: String = "",
    anonymous: String = "0",

    onClose: () -> Unit = {},
    popBackStack: () -> Unit = {},
    onAnonymousChange: (String) -> Unit = {},
    onTextChange: (String) -> Unit = {},
    onBoardChatSubmitClick: () -> Unit = {},
    onSituationChange: (String) -> Unit = {},
    onBoardDelete: () -> Unit = {},
    onNavigateToBoardScreen: () -> Unit = {},
    onBoardChatDelete: (String) -> Unit = {},
    onNeighborInformationClick: (String) -> Unit = {},

    photoDataList: List<Photo> = emptyList(),
    isPhotoLoading: Boolean = false,
    clickPhotoChange: (String) -> Unit = {},
    clickPhoto: String = "",

    ) {

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    when (situation) {
        "boardDelete" -> SimpleAlertDialog(
            onConfirmClick = onBoardDelete,
            onDismissClick = { onClose() },
            text = "게시물을 삭제하겠습니까?"
        )

        "deleteCheck" -> SimpleAlertDialog(
            onDismissOn = false,
            onConfirmClick = onNavigateToBoardScreen,
            text = "게시물이 삭제되었습니다."
        )
    }

    if(clickPhoto != "") {
        DiaryPhotoDialog(
            onClose = { clickPhotoChange("") },
            clickPhoto = clickPhoto
        )
    }

    Surface(modifier = Modifier.fillMaxSize()) {

        BackGroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /* ---------- 상단 ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                if (boardData.tag == userDataList.find { it.id == "auth" }?.value2) {
                    MainButton(
                        onClick = { onSituationChange("boardDelete") },
                        text = "삭제"
                    )
                }

                // 📌 게시판 타입 뱃지
                val (boardTitle, boardColor) = when (boardData.type) {
                    "congratulation" -> "축하 게시판" to Color(0xFFFFF3E0)
                    "worry" -> "고민 게시판" to Color(0xFFE3F2FD)
                    "friend" -> "친구 구하기" to Color(0xFFFFEBEE)
                    else -> "자유 게시판" to Color(0xFFF1F8E9)
                }

                Box(
                    modifier = Modifier
                        .background(boardColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = boardTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                }

                JustImage(
                    filePath = "etc/exit.png",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            popBackStack()
                        }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "이름을 누르면 프로필을 볼 수 있습니다",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            /* ---------- 게시글 ---------- */
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        color = when (boardData.type) {
                            "congratulation" -> Color(0xFFFFF3E0)
                            "worry" -> Color(0xFFE3F2FD)
                            "friend" -> Color(0xFFFFEBEE)
                            else -> Color(0xFFF1F8E9)
                        },
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(16.dp)
            ) {

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            text = if (boardData.anonymous == "1") "익명" else boardData.name,
                            fontSize = 20.sp,
                            color = when (boardData.type) {
                                "congratulation" -> Color(0xFF6D4C41)
                                "worry" -> Color(0xFF0D47A1)
                                "friend" -> Color(0xFFC2185B)
                                else -> Color(0xFF33691E)
                            },
                            modifier = Modifier.clickable {
                                if (boardData.anonymous != "1") {
                                    onNeighborInformationClick(boardData.tag)
                                }
                            }
                        )

                        if (boardData.anonymous != "1") {
                            Text(
                                text = " #${boardData.tag}",
                                fontSize = 15.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 타입 뱃지
                        Box(
                            modifier = Modifier
                                .background(
                                    color = when (boardData.type) {
                                        "congratulation" -> Color(0xFFFFCC80)
                                        "worry" -> Color(0xFF90CAF9)
                                        "friend" -> Color(0xFFF48FB1)
                                        else -> Color(0xFFAED581)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = when (boardData.type) {
                                    "congratulation" -> "축하"
                                    "worry" -> "고민"
                                    "friend" -> "친구 구해요"
                                    else -> "자유"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = boardData.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF333333),
                            lineHeight = 20.sp
                        )

                        if(boardData.photoLocalPath != "0"){
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 12.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    // 1. 기존 사진 리스트 출력
                                    items(photoDataList) { photo ->
                                        Box(
                                            modifier = Modifier
                                                .size(84.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(
                                                    1.dp,
                                                    Color.LightGray.copy(alpha = 0.5f),
                                                    RoundedCornerShape(12.dp)
                                                )
                                        ) {
                                            AsyncImage(
                                                model = photo.localPath,
                                                contentDescription = "일기 사진",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clickable { clickPhotoChange(photo.localPath) },
                                                contentScale = ContentScale.Crop
                                            )

                                        }
                                    }

                                    // 2. ⭐ 사진 업로드 중일 때 로딩 아이템 (첫 사진 추가 시에도 여기서 표시됨)
                                    if (isPhotoLoading) {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .size(84.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color.LightGray.copy(alpha = 0.3f))
                                                    .border(
                                                        1.dp,
                                                        Color.LightGray,
                                                        RoundedCornerShape(12.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(24.dp),
                                                        strokeWidth = 2.dp,
                                                        color = Color.Gray
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        "업로드 중..",
                                                        fontSize = 10.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 12.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .imePadding() // ⬅️ 키보드가 점유하는 공간만큼 하단 여백을 자동으로 만듭니다.
                    .bringIntoViewRequester(bringIntoViewRequester)
            ) {

                if(boardChat.isNotEmpty()){

                    /* ---------- 댓글 리스트 ---------- */
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(boardChat.reversed()) { chat ->

                            val displayName =
                                if (chat.anonymous == "1") "익명" else chat.name

                            // ⏰ timestamp → 7/24 12:15
                            val timeText = remember(chat.timestamp) {
                                try {
                                    val date = Date(chat.timestamp)
                                    val format = SimpleDateFormat(
                                        "M/dd HH:mm",
                                        Locale.getDefault()
                                    )
                                    format.format(date)
                                } catch (e: Exception) {
                                    ""
                                }
                            }

                            val bubbleColor = getPastelColorForTag(chat.tag)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (chat.anonymous == "1") {
                                            Color(0xFFF2F2F2).copy(alpha = 0.7f)   // 익명: 살짝 투명
                                        } else {
                                            bubbleColor.copy(alpha = 0.7f)       // 비익명: 파스텔 + 은은함
                                        },
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {

                                // 👤 상단: 이름 · 태그 / 시간 · 삭제
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // 왼쪽: 이름 + 태그
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                if(chat.anonymous != "1") { onNeighborInformationClick(chat.tag) }
                                        }
                                        ,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = displayName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF333333) // ⭐ 글자색 고정
                                        )

                                        if (chat.anonymous != "1") {
                                            Spacer(modifier = Modifier.width(6.dp))

                                            Text(
                                                text = "#${chat.tag}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF333333) // ⭐ 동일
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.weight(1f))

                                    // 오른쪽: 시간 + 삭제
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = timeText,
                                            fontSize = 10.sp,
                                            color = Color(0xFF333333) // ⭐ 동일
                                        )

                                        if (chat.tag == userDataList.find { it.id == "auth" }?.value2) {
                                            Spacer(modifier = Modifier.width(8.dp))

                                            Image(
                                                painter = painterResource(id = R.drawable.cancel),
                                                contentDescription = "삭제",
                                                modifier = Modifier
                                                    .size(13.dp)
                                                    .rotate(270f)
                                                    .clickable(
                                                        indication = null,
                                                        interactionSource = remember { MutableInteractionSource() }
                                                    ) {
                                                        onBoardChatDelete(chat.timestamp.toString())
                                                    }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // 💬 댓글 내용
                                Text(
                                    text = chat.message,
                                    fontSize = 14.sp,
                                    color = Color(0xFF333333), // ⭐ 고정
                                    lineHeight = 20.sp
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
                            text = "첫 댓글을 작성해주세요!",
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

                    Box (
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "익명",
                            style = MaterialTheme.typography.labelMedium
                        )

                        Checkbox(
                            checked = anonymous == "1",
                            onCheckedChange = {
                                        onAnonymousChange(if (it) "1" else "0")
                            }
                        )

                    }

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
            boardData = BoardMessage(),
            boardChat = listOf(BoardChatMessage())
        )
    }
}