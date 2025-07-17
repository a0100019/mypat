package com.a0100019.mypat.presentation.community

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.component.MainButton
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
        allAreaCount = communityState.allAreaCount,

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    situation : String = "world",
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
    allUserRankDataList: List<AllUser> = listOf(AllUser(), AllUser()),
    chatMessages: List<ChatMessage> = emptyList(),
    newChat: String = "",
    userDataList: List<User> = emptyList(),
    alertState: String = "",
    allAreaCount: String = "0",

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
            },
            allUserDataList = allUserDataList,
            allMapCount = allAreaCount
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

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = when (situation) {
                    "world" -> "마을 구경하기"
                    "chat" -> "채팅"
                    "firstGame" -> "게임1"
                    "secondGame" -> "게임2"
                    "thirdGameEasy" -> "게임3 - 쉬움"
                    "thirdGameNormal" -> "게임3 - 보통"
                    else -> "게임3 - 어려움"
                },
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .padding(top = 12.dp)
            )

            when (situation) {
                "world" -> Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                    ) {
                        CommunityWorldCard(
                            modifier = Modifier
                                .weight(1f),
                            userData = allUserData1,
                            worldDataList = allUserWorldDataList1,
                            patDataList = patDataList,
                            itemDataList = itemDataList,
                            onClick = { onUserWorldClick(1) }
                        )
                        CommunityWorldCard(
                            modifier = Modifier
                                .weight(1f),
                            userData = allUserData2,
                            worldDataList = allUserWorldDataList2,
                            patDataList = patDataList,
                            itemDataList = itemDataList,
                            onClick = { onUserWorldClick(2) }
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CommunityWorldCard(
                            modifier = Modifier
                                .weight(1f)
                            ,
                            userData = allUserData3,
                            worldDataList = allUserWorldDataList3,
                            patDataList = patDataList,
                            itemDataList = itemDataList,
                            onClick = { onUserWorldClick(3) }
                        )
                        CommunityWorldCard(
                            modifier = Modifier
                                .weight(1f),
                            userData = allUserData4,
                            worldDataList = allUserWorldDataList4,
                            patDataList = patDataList,
                            itemDataList = itemDataList,
                            onClick = { onUserWorldClick(4) }
                        )
                    }
                    MainButton(
                        onClick = onPageUpClick,
                        text = "   넘어가기   ",
                        modifier = Modifier
                            .padding(top = 6.dp)
                    )
                }

                "chat" -> Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 70.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(
                            color = MaterialTheme.colorScheme.onSecondary,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = 6.dp, end = 6.dp, top = 12.dp)
                        ,
                        reverseLayout = true,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(chatMessages.reversed()) { index, message ->
                            val isMine =
                                message.tag == userDataList.find { it.id == "auth" }!!.value2
                            val alignment = if (isMine) Arrangement.End else Arrangement.Start
                            val bubbleColor = if (isMine) MaterialTheme.colorScheme.scrim else Color(0xFFAEDFF7)
                            val bubbleBorderColor = if (isMine) MaterialTheme.colorScheme.primaryContainer else Color(0xFF4A90E2)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                                ,
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
                                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                                                Date(message.timestamp)
                                            )
                                        }

                                        Text(
                                            text = time,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                        )

                                        if (!isMine) {
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
                                            .border(
                                                width = 2.dp,
                                                color = bubbleBorderColor,
                                                shape = RoundedCornerShape(8.dp)
                                            )
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
                            .padding(top = 3.dp)
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newChat,
                            onValueChange = onChatTextChange,
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
                                )
                            ,
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

                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp) // 카드 간 간격
                ) {
                    itemsIndexed(allUserRankDataList) { index, user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                ,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${index+1}",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(0.1f)
                            )
                            
                            CommunityRankingCard(
                                userData = user,
                                situation = situation,
                                onClick = { onUserRankClick(index) },
                                modifier = Modifier
                                    .weight(0.9f)
                            )
                        }
                    }
                }

            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                    ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val thirdGameKeys = listOf("thirdGameEasy", "thirdGameNormal", "thirdGameHard")

            if (situation in thirdGameKeys) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    MainButton(
                        onClick = { onSituationChange("thirdGameEasy") },
                        text = "쉬움",
                        imageSize = 16.dp,
                        iconResId = if (situation == "thirdGameEasy") R.drawable.check else null,
                        modifier = Modifier.weight(1f)
                    )

                    MainButton(
                        onClick = { onSituationChange("thirdGameNormal") },
                        text = "보통",
                        imageSize = 16.dp,
                        iconResId = if (situation == "thirdGameNormal") R.drawable.check else null,
                        modifier = Modifier.weight(1f)
                    )

                    MainButton(
                        onClick = { onSituationChange("thirdGameHard") },
                        text = "어려움",
                        imageSize = 16.dp,
                        iconResId = if (situation == "thirdGameHard") R.drawable.check else null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            val buttons = listOf(
                "마을" to "world",
                "게임1" to "firstGame",
                "게임2" to "secondGame",
                "게임3" to "thirdGameEasy", // 대표 키만 지정
                "채팅" to "chat"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                buttons.forEach { (label, key) ->
                    val isSelected = when (key) {
                        "thirdGameEasy" -> false // situation in thirdGameKeys
                        else -> situation == key
                    }

                    Surface(
                        modifier = Modifier.weight(1f)
                    ) {
                        MainButton(
                            onClick = { onSituationChange(key) },
                            text = label,
                            iconResId = if (isSelected) R.drawable.check else null,
                            imageSize = 16.dp,
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
fun CommunityScreenPreview() {
    MypatTheme {
        CommunityScreen(
            userDataList = listOf(User(id = "auth")),
            chatMessages = listOf(ChatMessage(10202020, "a", "a", tag = "0", ban = "0"), ChatMessage(10202020, "a11", "a11", tag = "1", ban = "0"))
        )
    }
}