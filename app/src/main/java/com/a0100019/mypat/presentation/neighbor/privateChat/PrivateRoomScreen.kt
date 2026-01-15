package com.a0100019.mypat.presentation.neighbor.privateChat

import android.widget.Toast
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.neighbor.chat.getPastelColorForTag
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun PrivateRoomScreen(
    privateRoomViewModel: PrivateRoomViewModel = hiltViewModel(),
    onNavigateToPrivateChatInScreen: () -> Unit = {},
    onNavigateToMainScreen: () -> Unit = {},
    onNavigateToNeighborInformationScreen: () -> Unit = {},
    popBackStack: () -> Unit = {}

) {

    val privateRoomState : PrivateRoomState = privateRoomViewModel.collectAsState().value

    val context = LocalContext.current

    privateRoomViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is PrivateRoomSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            PrivateRoomSideEffect.NavigateToPrivateChatInScreen -> onNavigateToPrivateChatInScreen()
            PrivateRoomSideEffect.NavigateToMainScreen -> onNavigateToMainScreen()
            PrivateRoomSideEffect.NavigateToNeighborInformationScreen -> onNavigateToNeighborInformationScreen()
        }
    }

    PrivateRoomScreen(
        roomList = privateRoomState.roomList,
        userDataList = privateRoomState.userDataList,
        situation = privateRoomState.situation,
        yourTag = privateRoomState.yourTag,
        roomListTotalRank = privateRoomState.roomListTotalRank,

        popBackStack = popBackStack,
        onPrivateChatRoomClick = privateRoomViewModel::onPrivateChatRoomClick,
        onNavigateToMainScreen = onNavigateToMainScreen,
        onSituationChange = privateRoomViewModel::onSituationChange,
        onClose = privateRoomViewModel::onClose,
        onYourTagChange = privateRoomViewModel::onYourTagChange,
        loadMyRooms = privateRoomViewModel::loadMyRooms,
        onRankClick = privateRoomViewModel::onRankClick,
        onNeighborInformationClick = privateRoomViewModel::onNeighborInformationClick
    )
}

@Composable
fun PrivateRoomScreen(
    roomList: List<PrivateRoom> = emptyList(),
    roomListTotalRank: List<PrivateRoom> = emptyList(),
    userDataList: List<User> = emptyList(),
    situation: String = "",
    yourTag: String = "",

    popBackStack: () -> Unit = {},
    onPrivateChatRoomClick: (String) -> Unit = {},
    onNavigateToMainScreen: () -> Unit = {},
    onSituationChange: (String) -> Unit = {},
    onClose: () -> Unit = {},
    onYourTagChange: (String) -> Unit = {},
    loadMyRooms: () -> Unit = {},
    onRankClick: () -> Unit = {},
    onNeighborInformationClick: (String) -> Unit = {},
) {

    when(situation) {
        "roomCreate" -> {
            PrivateRoomCreateDialog(
                onClose = onClose,
                onTextChange = onYourTagChange,
                yourTag = yourTag,
                onConfirmClick = {
                    onNeighborInformationClick(yourTag)
                    onSituationChange("")
                }
            )
        }
        "rank" -> PrivateChatGameRankDialog(
            onClose = onClose,
            privateChatTotalRankList = roomListTotalRank
        )
        "gameQuestion" -> SimpleAlertDialog(
            onConfirmClick = {onSituationChange("")},
            onDismissOn = false,
            text = "친구와 함께 미니게임을 즐겨보세요!\n" +
                    "\n" +
                    "친구와 함께 점수를 쌓아 하루마을 최고의 친구가 되어보세요"
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        BackGroundImage()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                MainButton(
                    onClick = {
                        onSituationChange("roomCreate")
                    },
                    text = "친구 찾기"
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainButton(
                        onClick = {
                            onRankClick()
                            onSituationChange("rank")
                        },
                        text = "미니 게임 순위"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    JustImage(
                        filePath = "etc/question.png",
                        modifier = Modifier
                            .size(15.dp)
                            .clickable {
                                onSituationChange("gameQuestion")
                            }
                    )
                }

                MainButton(
                    onClick = onNavigateToMainScreen,
                    text = "닫기"
                )
            }

            if (roomList.isNotEmpty()) { // 🔥 방 목록
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        roomList.sortedByDescending { it.lastTimestamp }
                    ) { room ->

                        // 내가 user1이면 상대는 user2, 반대도 처리
                        val opponent =
                            if (room.user1 == userDataList.find { it.id == "auth" }!!.value2) {
                                room.name2
                            } else room.name1

                        val opponentTag =
                            if (room.user1 == userDataList.find { it.id == "auth" }!!.value2) {
                                room.user2
                            } else room.user1

                        val bubbleColor = getPastelColorForTag(opponentTag)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(14.dp))
                                .clickable { onPrivateChatRoomClick(room.roomId) }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // 🌈 왼쪽 컬러 버블 (프로필 느낌)
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(bubbleColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = opponent.first().toString(),
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // 이름 + 태그 + 마지막 메시지
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = opponent,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.Black
                                        )

                                        Spacer(modifier = Modifier.width(6.dp))

                                        // 🏷️ 태그 버블
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    bubbleColor.copy(alpha = 0.25f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "#$opponentTag",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                            )
                                        }

                                    }

                                    Text(
                                        text = room.lastMessage,
                                        fontSize = 14.sp,
                                        color = Color(0xFF666666),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // 🔔 안 읽은 메시지 수
                                if (room.attacker == userDataList.find { it.id == "auth" }?.value2) {

                                    Box(
                                            modifier = Modifier
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(0xFFE57373), // 파스텔 레드 테두리
                                                    shape = CircleShape
                                                )
                                                .background(
                                                    color = Color(0xFFFFEBEE), // 아주 연한 파스텔 레드 배경
                                                    shape = CircleShape
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "⚔️",
                                            color = Color(0xFF388E3C), // 진한 그린 글자
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // 🔔 안 읽은 메시지 수
                                if (room.messageCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .border(
                                                width = 1.dp,
                                                color = Color(0xFF81C784), // 파스텔 그린 테두리
                                                shape = CircleShape
                                            )
                                            .background(
                                                color = Color(0xFFE8F5E9), // 연한 파스텔 그린 배경
                                                shape = CircleShape
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = room.messageCount.toString(),
                                            color = Color(0xFF388E3C), // 진한 그린 글자
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                }
                            }
                        }

                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "마음에 드는 이웃과 친구를 맺어보세요!\n친구와 개인 채팅을 할 수 있으며 같이 보스를 잡아 랭킹에 오를 수 있습니다!",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrivateRoomScreenPreview() {
    MypatTheme {
        PrivateRoomScreen(
            roomList = listOf(
                PrivateRoom(
                roomId = "1_314", user1 = "1", user2 = "314",
                    name1 = "나",
                    name2 = "아쿠아",
                    lastMessage = "마지막",
                    messageCount = 3,
            )
            ),
            userDataList = listOf(
                User(id = "auth", value2 = "1")
            )

        )
    }
}