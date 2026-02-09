package com.a0100019.mypat.presentation.neighbor.board

import android.app.Activity
import android.net.Uri
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.photo.Photo
import com.a0100019.mypat.presentation.login.LoginLoadingDialog
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
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
fun BoardScreen(
    boardViewModel: BoardViewModel = hiltViewModel(),
    onNavigateToBoardMessageScreen: () -> Unit = {},
    onNavigateToMainScreen: () -> Unit = {},
    onNavigateToNeighborScreen: () -> Unit = {},

    popBackStack: () -> Unit = {},

    ) {

    val boardState : BoardState = boardViewModel.collectAsState().value

    val context = LocalContext.current
    val activity = context as Activity

    boardViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is BoardSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            BoardSideEffect.NavigateToBoardMessageScreen -> onNavigateToBoardMessageScreen()

//            BoardSideEffect.ShowRewardAd -> {
//                boardViewModel.showRewardAd(activity)
//            }
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
        loadBoardMessages = boardViewModel::loadBoardMessages,
        onNavigateToMainScreen = onNavigateToMainScreen,
//        onAdClick = boardViewModel::onAdClick,
        onBoardSubmitClick = boardViewModel::onBoardSubmitClick,
        onNavigateToNeighborScreen = onNavigateToNeighborScreen,

        onImageSelected = { uri ->
            // ✅ 여기서 뷰모델 호출!
            boardViewModel.handleImageSelection(context, uri)
        },
        isPhotoLoading = boardState.isPhotoLoading,
        photoLocalPath = boardState.photoLocalPath,
        photoDataList = boardState.photoDataList,
        deleteImage = boardViewModel::deleteImage
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
    loadBoardMessages: () -> Unit = {},
    onNavigateToMainScreen: () -> Unit = {},
    onAdClick: () -> Unit = {},
    onBoardSubmitClick: () -> Unit = {},
    onNavigateToNeighborScreen: () -> Unit = {},

    onImageSelected: (Uri) -> Unit = {}, // ✅ 사진 선택 콜백 추가
    isPhotoLoading: Boolean = false,
    photoLocalPath: String = "0",
    photoDataList: List<Photo> = emptyList(),
    deleteImage: (Photo) -> Unit = {},

    ) {

    when (situation) {
        "boardSubmit" -> BoardSubmitDialog(
            text = text,
            anonymous = boardAnonymous,
            type = boardType,
            onClose = onClose,
            onChangeAnonymousClick = onBoardAnonymousChange,
            onChangeTypeClick = onBoardTypeChange,
            onTextChange = onTextChange,
            onConfirmClick = {
                onSituationChange("boardSubmitCheck")
            },
            onImageSelected = onImageSelected,
            photoDataList = photoDataList,
            deleteImage = deleteImage,
            photoLocalPath = photoLocalPath
        )

        "boardSubmitCheck" -> SimpleAlertDialog(
            onConfirmClick = onBoardSubmitClick,
            onDismissClick = {
                onSituationChange("boardSubmit")
            },
            text = "하루마을은 평화로운 커뮤니티를 지향하며, 전체 이용가인 만큼 부적절한 내용은 삼가해 주시기 바랍니다.\n\n게시글을 작성하겠습니까?",
        )

        "boardSubmitConfirm" -> BoardSubmitConfirmDialog(
            onDismissClick = {
                onClose()
                loadBoardMessages()
            }
        )
    }

    if(isPhotoLoading) {
        LoginLoadingDialog()
    }

    Surface(modifier = Modifier.fillMaxSize()) {

        BackGroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "자유게시판",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                JustImage(
                    filePath = "etc/write.png",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onSituationChange("boardSubmit") }
                )

                MainButton(
                    onClick = {
                        if (situation == "myBoard") onSituationChange("")
                        else onSituationChange("myBoard")
                    },
                    text = if (situation == "myBoard") "내 게시물" else "전체 게시물"
                )

                JustImage(
                    filePath = "etc/exit.png",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onNavigateToNeighborScreen()
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(
                    if (situation == "myBoard")
                        myBoardMessages.reversed()
                    else
                        boardMessages.reversed()
                ) { message ->

                    val displayName =
                        if (message.anonymous == "1") "익명" else message.name

                    val timeText = remember(message.timestamp) {
                        val sdf = SimpleDateFormat("M/d HH:mm", Locale.getDefault())
                        sdf.format(Date(message.timestamp))
                    }

                    // 사진 존재 여부 체크 (photoFirebaseUrl이 "0"이 아니면 사진 있음)
                    val hasPhoto = message.photoFirebaseUrl != "0"

                    // ✅ 전체를 Box로 감싸서 아이콘을 위에 띄움
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp) // 아이콘이 튀어나올 공간 확보
                    ) {
                        // 1. 기존 메시지 박스
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (hasPhoto) Color(0xFFFFFFFF) else Color(0xFFF8F8F8),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .border(
                                    width = if (hasPhoto) 2.dp else 1.dp,
                                    color = if (hasPhoto) Color(0xFFFFD700) else Color(0xFFE6E6E6),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    onBoardMessageClick(message.timestamp.toString())
                                }
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val typeEmoji = when (message.type) {
                                        "congratulation" -> "🎉"
                                        "worry" -> "💭"
                                        "friend" -> "👫"
                                        else -> "🌱"
                                    }

                                    val typeBackgroundColor = when (message.type) {
                                        "congratulation" -> Color(0xFFFFF1CC)
                                        "worry" -> Color(0xFFE6F1FB)
                                        "friend" -> Color(0xFFFFE6F0)
                                        else -> Color(0xFFEAF4EC)
                                    }

                                    val typeText = when (message.type) {
                                        "congratulation" -> "축하"
                                        "worry" -> "고민"
                                        "friend" -> "친구 구해요"
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

                                    Text(
                                        text = timeText,
                                        fontSize = 11.sp,
                                        color = Color(0xFF999999)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = message.message,
                                fontSize = 14.sp,
                                color = Color(0xFF333333),
                                lineHeight = 20.sp,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // 2. ✅ 왼쪽 위로 살짝 튀어나온 사진 아이콘 배지
                        if (hasPhoto) {
                            Box(
                                modifier = Modifier
                                    .offset(x = (-4).dp, y = (-20).dp) // 왼쪽(-4) 위(-12)로 튀어나오게 조정
                                    .background(
                                        color = Color(0xFFFFD700), // 테두리와 맞춘 골드 색상
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(2.dp, Color.White, RoundedCornerShape(8.dp)) // 흰색 테두리로 분리감 줌
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "📸 PHOTO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                if(boardMessages.isEmpty()){
                    item {
                        Text(
                            text = "로딩중..",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
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