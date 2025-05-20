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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter.All

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

        onPageUpClick = communityViewModel::opPageUpClick,
        onUserClick = communityViewModel::onUserClick,
        onLikeClick = communityViewModel::onLikeClick,
        onSituationChange = communityViewModel::onSituationChange,
        onChatTextChange = communityViewModel::onChatTextChange,
        onChatSubmitClick = communityViewModel::onChatSubmitClick
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

    onPageUpClick: () -> Unit = {},
    onUserClick: (Int) -> Unit = {},
    onLikeClick: () -> Unit = {},
    onSituationChange: (String) -> Unit = {},
    onChatTextChange: (String) -> Unit = {},
    onChatSubmitClick: () -> Unit = {}

) {

    if(clickAllUserData.id != 0) {
        CommunityUserDialog(
            onClose = { onUserClick(0) },
            clickAllUserData = clickAllUserData,
            clickAllUserWorldDataList = clickAllUserWorldDataList,
            patDataList = patDataList,
            itemDataList = itemDataList,
            onLikeClick = {
                onLikeClick()
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "마을 구경하기"
        )

        when (situation) {
            "world" -> Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CommunityWorldCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onUserClick(1)
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
                                onUserClick(2)
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
                                onUserClick(3)
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
                                onUserClick(4)
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
                // 메시지 리스트
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(chatMessages.reversed()) { message ->
                        // 말풍선 UI
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = message.name,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(message.message)
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
                items(allUserRankDataList) { user ->
                    CommunityRankingCard(
                        userData = user,
                        situation = situation
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
            situation = "chat",
            chatMessages = listOf(ChatMessage(10202020, "a", "a"))
        )
    }
}