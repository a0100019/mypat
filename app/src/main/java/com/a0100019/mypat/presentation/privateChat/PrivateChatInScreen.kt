package com.a0100019.mypat.presentation.privateChat

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.neighbor.chat.ChatSideEffect
import com.a0100019.mypat.presentation.neighbor.chat.getPastelColorForTag
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PrivateChatInScreen(
    privateChatInViewModel: PrivateChatInViewModel = hiltViewModel(),
    popBackStack: () -> Unit = {},
    onNavigateToPrivateRoomScreen: () -> Unit = {},
    onNavigateToNeighborInformationScreen: () -> Unit = {},

) {

    val privateChatInState : PrivateChatInState = privateChatInViewModel.collectAsState().value

    val context = LocalContext.current

    privateChatInViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is PrivateChatInSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            PrivateChatInSideEffect.NavigateToPrivateRoomScreen -> onNavigateToPrivateRoomScreen()
            PrivateChatInSideEffect.NavigateToNeighborInformationScreen -> onNavigateToNeighborInformationScreen()

        }
    }

    PrivateChatInScreen(
        userDataList = privateChatInState.userDataList,
        chatMessages = privateChatInState.chatMessages,
        text = privateChatInState.text,
        yourName = privateChatInState.yourName,
        yourTag = privateChatInState.yourTag,
        situation = privateChatInState.situation,

        popBackStack = popBackStack,
        onTextChange = privateChatInViewModel::onTextChange,
        onChatSubmitClick = privateChatInViewModel::onChatSubmitClick,
        onNavigateToPrivateRoomScreen = onNavigateToPrivateRoomScreen,
        onNeighborInformationClick = privateChatInViewModel::onNeighborInformationClick,
        onSituationChange = privateChatInViewModel::onSituationChange,
        onPrivateRoomDelete = privateChatInViewModel::onPrivateRoomDelete,
        onClose = privateChatInViewModel::onClose
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatInScreen(
    userDataList: List<User> = emptyList(),
    chatMessages: List<PrivateChatMessage> = emptyList(),
    text: String = "",
    yourName: String = "이웃",
    yourTag: String = "0",
    situation: String = "",

    popBackStack: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
    onChatSubmitClick: () -> Unit = {},
    onNavigateToPrivateRoomScreen: () -> Unit = {},
    onNeighborInformationClick: () -> Unit = {},
    onSituationChange: (String) -> Unit = {},
    onPrivateRoomDelete: () -> Unit = {},
    onClose: () -> Unit = {},
) {

    when(situation) {
        "roomDelete" -> SimpleAlertDialog(
            onConfirmClick = onPrivateRoomDelete,
            onDismissClick = {
                onClose()
            },
            text = "친구 삭제하겠습니까?\n되돌릴 수 없습니다. 신중하게 생각하세요"
        )
        "deleteCheck" -> SimpleAlertDialog(
            onDismissOn = false,
            onConfirmClick = onNavigateToPrivateRoomScreen,
            text = "삭제되었습니다."
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Box(
            modifier = Modifier.padding(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {

                    // 오른쪽 버튼
                    MainButton(
                        text = "삭제",
                        onClick = {
                            onSituationChange("roomDelete")
                        },

                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                onNeighborInformationClick()
                            }
                    ) {

                        Text(
                            text = yourName,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "#$yourTag",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                        )
                    }
                    // 오른쪽 버튼
                    MainButton(
                        text = "닫기",
                        onClick = onNavigateToPrivateRoomScreen,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 70.dp)
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

                    if(chatMessages.isNotEmpty()){
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(start = 6.dp, end = 6.dp, top = 12.dp),
                            reverseLayout = true,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(chatMessages.reversed()) { index, message ->

                                val isMine = message.tag == userDataList.find { it.id == "auth" }!!.value2

                                val alignment = when {
                                    isMine -> Arrangement.End
                                    else -> Arrangement.Start
                                }

                                val bubbleColor = getPastelColorForTag(message.tag)

                                val textColor = Color.Black

                                val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                                val today = dateFormat.format(Date())

                                val prevDate = chatMessages.reversed().getOrNull(index - 1)
                                val currentDate = dateFormat.format(Date(message.timestamp))
                                val previousDate = prevDate?.let { dateFormat.format(Date(it.timestamp)) }

                                // 📅 날짜 구분선
                                if (currentDate != previousDate && currentDate != today) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = SimpleDateFormat("MM월 dd일 E요일", Locale.KOREA)
                                                .format(Date(message.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                    // 일반 채팅
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp),
                                        horizontalArrangement = alignment
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .widthIn(max = 280.dp)
                                                .padding(horizontal = 8.dp),
                                            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
                                        ) {
                                            Row {
                                                Row(
                                                    modifier = Modifier.clickable {
//                                                        onUserRankClick(message.tag.toInt())
                                                    }
                                                ) {
                                                    Text(
                                                        text = message.name,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                                    )
                                                    Text(
                                                        text = "#" + message.tag,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                                    )
                                                }

                                                val time = remember(message.timestamp) {
                                                    SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
                                                        .format(Date(message.timestamp))
                                                }

                                                Text(
                                                    text = time,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                                )

                                            }

                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        bubbleColor,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(8.dp)
                                            ) {
                                                Text(text = message.message)
                                            }
                                        }
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
                                    onChatSubmitClick()
                                }
                        )

                    }

                }


            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun PrivateChatScreenInPreview() {
    MypatTheme {
        PrivateChatInScreen(

            userDataList = listOf(
                User(id = "auth", value2 = "1")
            )

        )
    }
}