package com.a0100019.mypat.presentation.community

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommunityScreen(
    communityViewModel: CommunityViewModel = hiltViewModel()

) {

    val communityState : CommunityState = communityViewModel.collectAsState().value

    val context = LocalContext.current

    communityViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CommunitySideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    CommunityScreen(
        situation = communityState.situation,
        patDataList = communityState.patDataList,
        itemDataList = communityState.itemDataList,
        page = communityState.page,
        allUserDataList = communityState.allUserDataList,
        allUserData1 = communityState.allUserData1,
        allUserData2 = communityState.allUserData2,
        allUserData3 = communityState.allUserData3,
        allUserData4 = communityState.allUserData4,
        allUserWorldDataList1 = communityState.allUserWorldDataList1,
        allUserWorldDataList2 = communityState.allUserWorldDataList2,
        allUserWorldDataList3 = communityState.allUserWorldDataList3,
        allUserWorldDataList4 = communityState.allUserWorldDataList4,
        clickAllUserData = communityState.clickAllUserData,
        clickAllUserWorldDataList = communityState.clickAllUserWorldDataList,
        allUserRankDataList = communityState.allUserRankDataList,
        chatMessages = communityState.chatMessages,
        newChat = communityState.newChat,
        userDataList = communityState.userDataList,
        alertState = communityState.alertState,

        onPageUpClick = communityViewModel::opPageUpClick,
        onUserWorldClick = communityViewModel::onUserWorldClick,
        onLikeClick = communityViewModel::onLikeClick,
        onSituationChange = communityViewModel::onSituationChange,
        onChatTextChange = communityViewModel::onChatTextChange,
        onChatSubmitClick = communityViewModel::onChatSubmitClick,
        onUserRankClick = communityViewModel::onUserRankClick,
        onBanClick = communityViewModel::onBanClick,
        alertStateChange = communityViewModel::alertStateChange
    )
}


@Composable
fun CommunityScreen(
    situation : String,
    patDataList: List<Pat> = emptyList(),
    itemDataList: List<Item> = emptyList(),
    page: Int = 0,
    allUserDataList: List<AllUser> = emptyList(),
    allUserData1: AllUser = AllUser(),
    allUserData2: AllUser = AllUser(),
    allUserData3: AllUser = AllUser(),
    allUserData4: AllUser = AllUser(),
    allUserWorldDataList1: List<String> = emptyList(),
    allUserWorldDataList2: List<String> = emptyList(),
    allUserWorldDataList3: List<String> = emptyList(),
    allUserWorldDataList4: List<String> = emptyList(),
    clickAllUserData: AllUser = AllUser(),
    clickAllUserWorldDataList: List<String> = emptyList(),
    allUserRankDataList: List<AllUser> = emptyList(),
    chatMessages: List<ChatMessage> = emptyList(),
    newChat: String = "",
    userDataList: List<User> = emptyList(),
    alertState: String = "",

    onPageUpClick: () -> Unit = {},
    onUserWorldClick: (Int) -> Unit = {},
    onLikeClick: () -> Unit = {},
    onSituationChange: (String) -> Unit = {},
    onChatTextChange: (String) -> Unit = {},
    onChatSubmitClick: () -> Unit = {},
    onUserRankClick: (Int) -> Unit = {},
    onBanClick: (Int) -> Unit = {},
    alertStateChange: (String) -> Unit = {},

) {

    if(clickAllUserData.id != 0) {
        CommunityUserDialog(
            onClose = { onUserWorldClick(0) },
            clickAllUserData = clickAllUserData,
            clickAllUserWorldDataList = clickAllUserWorldDataList,
            patDataList = patDataList,
            itemDataList = itemDataList,
            onLikeClick = {
                onLikeClick()
            },
            onBanClick = {
                alertStateChange("-1")
            }
        )
    }

    if(alertState != "") {
        SimpleAlertDialog(
            onConfirm = {
                onBanClick(alertState.toInt())
                alertStateChange("")
            },
            onDismiss = { alertStateChange("") },
            text = "신고하시겠습니까?"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "마을 구경하기",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(top = 12.dp)
        )

        when (situation) {
            "world" -> Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f).padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CommunityWorldCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onUserWorldClick(1)
                            },
                        userData = allUserData1,
                        worldDataList = allUserWorldDataList1,
                        patDataList = patDataList,
                        itemDataList = itemDataList
                    )
                    CommunityWorldCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onUserWorldClick(2)
                            },
                        userData = allUserData2,
                        worldDataList = allUserWorldDataList2,
                        patDataList = patDataList,
                        itemDataList = itemDataList
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CommunityWorldCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onUserWorldClick(3)
                            },
                        userData = allUserData3,
                        worldDataList = allUserWorldDataList3,
                        patDataList = patDataList,
                        itemDataList = itemDataList
                    )
                    CommunityWorldCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onUserWorldClick(4)
                            },
                        userData = allUserData4,
                        worldDataList = allUserWorldDataList4,
                        patDataList = patDataList,
                        itemDataList = itemDataList
                    )
                }
                Button(
                    onClick = onPageUpClick
                ) {
                    Text("다음")
                }
            }

            "chat" ->  Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(chatMessages.reversed()) { index, message ->
                        val isMine = message.tag == userDataList.find { it.id == "auth"}!!.value2
                        val alignment = if (isMine) Arrangement.End else Arrangement.Start
                        val bubbleColor = if (isMine) Color(0xFFBBDEFB) else Color(0xFFE0E0E0)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = alignment
                        ) {
                            Column(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .padding(horizontal = 8.dp),
                                horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
                            ) {
                                Row {
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

                                    // 시간 포맷
                                    val time = remember(message.timestamp) {
                                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
                                    }

                                    Text(
                                        text = time,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                    )

                                    if(!isMine) {
                                        JustImage(
                                            filePath = "etc/ban.png",
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clickable {
                                                    alertStateChange(index.toString())
                                                }
                                        )
                                    }

                                }
                                Box(
                                    modifier = Modifier
                                        .background(bubbleColor, RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(text = message.message)
                                }
                            }
                        }
                    }
                }


                // 입력창 + 전송버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newChat,
                        onValueChange = onChatTextChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("메시지를 입력하세요") },
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onChatSubmitClick
                    ) {
                        Text("전송")
                    }
                }
            }

            else -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // 카드 간 간격
            ) {
                itemsIndexed(allUserRankDataList) { index, user ->
                    CommunityRankingCard(
                        userData = user,
                        situation = situation,
                        modifier = Modifier
                            .clickable {
                                onUserRankClick(index)
                            }
                    )
                }
            }

        }

        if(situation in listOf("thirdGameEasy", "thirdGameNormal", "thirdGameHard")) {
            Row {

                Button(
                    onClick = {
                        onSituationChange("thirdGameEasy")
                    },
                    enabled = situation != "thirdGameEasy"
                ) {
                    Text("쉬움")
                }

                Button(
                    onClick = {
                        onSituationChange("thirdGameNormal")
                    },
                    enabled = situation != "thirdGameNormal"
                ) {
                    Text("보통")
                }

                Button(
                    onClick = {
                        onSituationChange("thirdGameHard")
                    },
                    enabled = situation != "thirdGameHard"
                ) {
                    Text("어려움")
                }

            }
        }

        Row {
            Button(
                onClick = {
                    onSituationChange("world")
                },
                enabled = situation != "world"
            ) {
                Text("마을")
            }

            Button(
                onClick = {
                    onSituationChange("firstGame")
                },
                enabled = situation != "firstGame"
            ) {
                Text("게임1")
            }

            Button(
                onClick = {
                    onSituationChange("secondGame")
                },
                enabled = situation != "secondGame"
            ) {
                Text("게임2")
            }

            Button(
                onClick = {
                    onSituationChange("thirdGameEasy")
                },
                enabled = situation !in listOf("thirdGameEasy", "thirdGameNormal", "thirdGameHard")
            ) {
                Text("게임3")
            }

            Button(
                onClick = {
                    onSituationChange("chat")
                },
                enabled = situation != "chat"
            ) {
                Text("채팅")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview() {
    MypatTheme {
        CommunityScreen(
            situation = "world",
            userDataList = listOf(User(id = "auth")),
            chatMessages = listOf(ChatMessage(10202020, "a", "a", tag = "0", ban = "0"), ChatMessage(10202020, "a11", "a11", tag = "1", ban = "0"))
        )
    }
}