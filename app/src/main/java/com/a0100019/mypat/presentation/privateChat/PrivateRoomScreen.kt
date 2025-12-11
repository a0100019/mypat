package com.a0100019.mypat.presentation.privateChat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun PrivateRoomScreen(
    privateRoomViewModel: PrivateRoomViewModel = hiltViewModel(),
    onNavigateToPrivateChatInScreen: () -> Unit = {},
    popBackStack: () -> Unit = {}

) {

    val privateRoomState : PrivateRoomState = privateRoomViewModel.collectAsState().value

    val context = LocalContext.current

    privateRoomViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is PrivateRoomSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            PrivateRoomSideEffect.NavigateToPrivateChatInScreen -> onNavigateToPrivateChatInScreen()
        }
    }

    PrivateRoomScreen(
        roomList = privateRoomState.roomList,
        userDataList = privateRoomState.userDataList,

        popBackStack = popBackStack,
        onPrivateChatRoomClick = privateRoomViewModel::onPrivateChatRoomClick
    )
}

@Composable
fun PrivateRoomScreen(
    roomList: List<PrivateRoom> = emptyList(),
    userDataList: List<User> = emptyList(),

    popBackStack: () -> Unit = {},
    onPrivateChatRoomClick: (String) -> Unit = {}
) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        BackGroundImage()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(modifier = Modifier.padding(12.dp)) {
                MainButton(
                    onClick = popBackStack,
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
                    items(roomList) { room ->

                        // 내가 user1이면 상대는 user2, 반대도 처리
                        val opponent =
                            if (room.user1 == userDataList.find { it.id == "auth" }!!.value2) {
                                room.name2
                            } else room.name1

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEFEFEF), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                                .clickable {
                                    onPrivateChatRoomClick(room.roomId)
                                }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {

                                // 상대 이름
                                Text(
                                    text = opponent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                // 마지막 메시지
                                Text(
                                    text = room.lastMessage,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )

                                Text(
                                    text = room.messageCount.toString(),
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )

                            }
                        }
                    }
                }
            } else {
                Column {
                    Text(
                        text = "개인 채팅을 해보세요!"
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