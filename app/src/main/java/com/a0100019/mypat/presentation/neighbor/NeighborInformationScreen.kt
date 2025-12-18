package com.a0100019.mypat.presentation.neighbor

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.MusicPlayer
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun NeighborInformationScreen(
    neighborInformationViewModel: NeighborInformationViewModel = hiltViewModel(),
    onNavigateToPrivateRoomScreen: () -> Unit = {},

    popBackStack: () -> Unit = {},
) {

    val neighborInformationState : NeighborInformationState = neighborInformationViewModel.collectAsState().value

    val context = LocalContext.current

    neighborInformationViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is NeighborInformationSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            NeighborInformationSideEffect.NavigateToPrivateRoomScreen -> onNavigateToPrivateRoomScreen()
        }
    }

    NeighborInformationScreen(
        clickAllUserData = neighborInformationState.clickAllUserData,
        clickAllUserWorldDataList = neighborInformationState.clickAllUserWorldDataList,
        patDataList = neighborInformationState.patDataList,
        itemDataList = neighborInformationState.itemDataList,
        allMapCount = neighborInformationState.allAreaCount,
        allUserDataList = neighborInformationState.allUserDataList,
        situation = neighborInformationState.situation,

        onClose = neighborInformationViewModel::onClose,
        popBackStack = popBackStack,
        onLikeClick = neighborInformationViewModel::onLikeClick,
        onBanClick = neighborInformationViewModel::onBanClick,
        onPrivateChatStartClick = neighborInformationViewModel::onPrivateChatStartClick,

    )
}

@Composable
fun NeighborInformationScreen(
    clickAllUserData: AllUser = AllUser(),
    clickAllUserWorldDataList: List<String> = emptyList(),
    patDataList: List<Pat> = emptyList(),
    itemDataList: List<Item> = emptyList(),
    allUserDataList: List<AllUser> = emptyList(),
    allMapCount: String = "0",
    situation: String = "",

    onClose : () -> Unit = {},
    popBackStack: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onBanClick: (Int) -> Unit = {},
    onPrivateChatStartClick: () -> Unit = {},

    ) {

    var page by remember { mutableIntStateOf(1) }

    when(situation) {
        "privateChat" -> SimpleAlertDialog(
            onConfirmClick = onPrivateChatStartClick,
            onDismissClick = onClose,
            text = "개인 채팅을 시작하시겠습니까?"
        )
    }

    //빈 데이터일 경우
    if(clickAllUserData.firstDate == "0") {

        Dialog(
            onDismissRequest = onClose
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(340.dp)
                        .shadow(12.dp, RoundedCornerShape(24.dp))
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "#" + clickAllUserData.tag
                            ,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.size(15.dp))

                        Text(
                            text = "아직 업데이트 되지 않은 이용자입니다." +
                                    "\n내일 다시 방문해주세요"
                            ,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 6.dp, end = 6.dp, top = 6.dp)
                        ) {

                            MainButton(
                                text = "1대1 채팅하기",
                                onClick = onPrivateChatStartClick
                            )

                            Spacer(modifier = Modifier.size(60.dp))

                            MainButton(
                                text = "닫기",
                                onClick = popBackStack
                            )
                        }
                    }
                }
            }
        }

    } else {
        val introduction =
            clickAllUserData
                .warning
                .split("@")
                .first()

        val medalList: List<Int> =
            clickAllUserData
                .warning
                .split("@")
                .last()
                .split("/")              // ["1","3","12","5"]
                .mapNotNull { it.toIntOrNull() } // [1,3,12,5]

        MusicPlayer(
            music = clickAllUserData.area
        )

        // 이름, 좋아요
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = clickAllUserData.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(start = 10.dp, end = 6.dp)
                )
                Text(
                    text = " #" + clickAllUserData.tag,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Text(
                text = "좋아요 ${clickAllUserData.like}개",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(end = 10.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 6.dp, top = 6.dp)
        ) {

            JustImage(
                filePath = "etc/ban.png",
                modifier = Modifier
                    .clickable {
                        onBanClick(-1)
                    }
                    .size(15.dp)
            )

            MainButton(
                text = "좋아요 누르기",
                onClick = onLikeClick
            )

            MainButton(
                text = "1대1 채팅하기",
                onClick = onPrivateChatStartClick
            )

            MainButton(
                text = "닫기",
                onClick = popBackStack
            )
        }

    }

//    val myMedalString = userDataList.find { it.id == "etc" }?.value3 ?: ""
//
//    val myMedalList: List<Int> =
//        myMedalString
//            .split("/")              // ["1","3","12","5"]
//            .mapNotNull { it.toIntOrNull() } // [1,3,12,5]
//
//    when(situation) {
//        "medal" -> {
//            MedalChangeDialog(
//                onClose = onClose,
//                onMedalClick = onMedalChangeClick,
//                userDataList = userDataList
//            )
//        }
//        "introduction" -> {
//            IntroductionChangeDialog(
//                onClose = onClose,
//                onTextChange = onTextChange,
//                text = text,
//                onConfirmClick = onIntroductionChangeClick
//            )
//        }
//    }
//
//    Surface (
//        modifier = Modifier
//            .fillMaxSize()
//        ,
//        shape = RoundedCornerShape(16.dp),
//        color = Color(0xFFFFF8E7),
//        border = BorderStroke(2.dp, Color(0xFF5A3A22)),
//        shadowElevation = 8.dp,
//    ){
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp),
//            verticalArrangement = Arrangement.SpaceBetween,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // 이름, 좋아요
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 6.dp)
//                ,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "${userDataList.find { it.id == "name" }?.value}",
//                        style = MaterialTheme.typography.titleLarge,
//                        modifier = Modifier
//                            .padding(start = 10.dp, end = 6.dp)
//                    )
//                    Text(
//                        text = "#${userDataList.find { it.id == "auth" }?.value2}",
//                        style = MaterialTheme.typography.titleSmall
//                    )
//                }
//                Spacer(modifier = Modifier.weight(1f))
//                Text(
//                    text = "좋아요 ${userDataList.find { it.id == "community" }?.value}개",
//                    style = MaterialTheme.typography.titleSmall,
//                    modifier = Modifier
//                        .padding(end = 10.dp)
//                )
//                MainButton(
//                    text = "닫기",
//                    onClick = popBackStack
//                )
//            }
//
//            if(page == 0) {
//                Text(
//                    text = medalName(myMedalList[0])
//                )
//
//                // 미니맵 뷰
//                Surface(
//                    modifier = Modifier
//                        .aspectRatio(1f / 1.25f),
//                    shape = RoundedCornerShape(16.dp),
//                    color = Color(0xFFFFF8E7),
//                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer),
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.White), // Optional: Set background color
//                        contentAlignment = Alignment.Center // Center content
//                    ) {
//                        JustImage(
//                            filePath = areaUrl,
//                            modifier = Modifier.fillMaxSize(),
//                            contentScale = ContentScale.FillBounds
//                        )
//
//                        BoxWithConstraints(
//                            modifier = Modifier.fillMaxSize()
//                        ) {
//                            val density = LocalDensity.current
//
//                            // Surface 크기 가져오기 (px → dp 변환)
//                            val surfaceWidth = constraints.maxWidth
//                            val surfaceHeight = constraints.maxHeight
//
//                            val surfaceWidthDp = with(density) { surfaceWidth.toDp() }
//                            val surfaceHeightDp = with(density) { surfaceHeight.toDp() }
//
//                            worldDataList.forEachIndexed { index, worldData ->
//                                key("${worldData.id}_${worldData.type}") {
//                                    if (worldData.type == "pat") {
//                                        patDataList.find { it.id.toString() == worldData.value }
//                                            ?.let { patData ->
//
//                                                PatImage(
//                                                    patUrl = patData.url,
//                                                    surfaceWidthDp = surfaceWidthDp,
//                                                    surfaceHeightDp = surfaceHeightDp,
//                                                    xFloat = patData.x,
//                                                    yFloat = patData.y,
//                                                    sizeFloat = patData.sizeFloat,
//                                                    effect = patData.effect,
//                                                    onClick = { }
//                                                )
//
//                                            }
//
//                                    } else {
//                                        itemDataList.find { it.id.toString() == worldData.value }
//                                            ?.let { itemData ->
//                                                WorldItemImage(
//                                                    itemUrl = itemData.url,
//                                                    surfaceWidthDp = surfaceWidthDp,
//                                                    surfaceHeightDp = surfaceHeightDp,
//                                                    xFloat = itemData.x,
//                                                    yFloat = itemData.y,
//                                                    sizeFloat = itemData.sizeFloat
//                                                )
//
//                                            }
//
//                                    }
//                                }
//                            }
//
//                        }
//
//                    }
//                }
//
//                Column(
//                    modifier = Modifier
//                        .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
//                    verticalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Surface(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .border(
//                                width = 2.dp,
//                                color = MaterialTheme.colorScheme.primaryContainer,
//                                shape = RoundedCornerShape(16.dp)
//                            ),
//                        shape = RoundedCornerShape(16.dp),
//                        tonalElevation = 2.dp,
//                        color = MaterialTheme.colorScheme.scrim
//                    ) {
//
//                        Column(
//                            modifier = Modifier
//                                .padding(12.dp)
//                        ) {
//                            Text(
//                                text = userDataList.find { it.id == "etc" }?.value ?: "",
//                                textAlign = TextAlign.Center,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                            )
//                        }
//
//                    }
//                }
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceAround
//                ) {// 접속 정보
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "마을 탄생일",
//                            style = MaterialTheme.typography.labelMedium,
//                            modifier = Modifier
//                                .padding(end = 6.dp)
//                        )
//                        Text(
//                            text = userDataList.find { it.id == "date" }?.value3 ?: "2015-03-12",
//                            style = MaterialTheme.typography.labelMedium
//                        )
//                    }
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "접속일",
//                            style = MaterialTheme.typography.labelMedium,
//                            modifier = Modifier
//                                .padding(end = 6.dp)
//                        )
//                        Text(
//                            text = "${userDataList.find { it.id == "date" }?.value2 ?: "-"}일",
//                            style = MaterialTheme.typography.labelMedium
//                        )
//                    }
//                }
//            } else {
//
//                Surface(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .aspectRatio(1f / 1.25f)
//                        .padding(6.dp),
//                    shape = RoundedCornerShape(16.dp),
//                    color = Color(0xFFFFF8E7),
//                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer),
//                ) {
//                    LazyVerticalGrid(
//                        columns = GridCells.Fixed(3), // 한 줄에 3개
//                        modifier = Modifier.fillMaxSize(),
//                        contentPadding = PaddingValues(8.dp),
//                        horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//
//                        //칭호개수 +1 만큼 아이템크기
//                        items(16) { index ->
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .background(
//                                        color = MaterialTheme.colorScheme.surface,
//                                        shape = RoundedCornerShape(12.dp)
//                                    )
//                                    .border(
//                                        1.dp,
//                                        MaterialTheme.colorScheme.outline,
//                                        RoundedCornerShape(12.dp)
//                                    )
//                                    .padding(vertical = 12.dp),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                TextAutoResizeSingleLine(
//                                    text = medalName(index+1),
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                )
//                                if (myMedalList.contains(index+1)) {
//                                    Text(
//                                        text = "획득",
//                                        style = MaterialTheme.typography.titleMedium,
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//
//                Column(
//                    modifier = Modifier,
//                    verticalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Surface(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .border(
//                                width = 2.dp,
//                                color = MaterialTheme.colorScheme.primaryContainer,
//                                shape = RoundedCornerShape(16.dp)
//                            ),
//                        shape = RoundedCornerShape(16.dp),
//                        tonalElevation = 2.dp,
//                        color = MaterialTheme.colorScheme.scrim
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .padding(horizontal = 16.dp, vertical = 12.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(
//                                text = "도감",
//                                style = MaterialTheme.typography.titleLarge,
//                                modifier = Modifier
//                                    .padding(bottom = 6.dp)
//                            )
//
//                            Row(
//                                horizontalArrangement = Arrangement.SpaceAround,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                            ) {
//                                Row {
//                                    Text(
//                                        text = "펫",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = "${allPatDataList.count { it.date != "0" }}/${allPatDataList.size}",
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//
//                                Row {
//                                    Text(
//                                        text = "아이템",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = "${allItemDataList.count { it.date != "0" } - 20}/${allItemDataList.size - 20}",
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//
//                                Row {
//                                    Text(
//                                        text = "맵",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = "${allAreaDataList.count { it.date != "0" }}/${allAreaDataList.size}",
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//                            }
//
//                            Divider(
//                                color = Color.LightGray,
//                                thickness = 1.dp,
//                                modifier = Modifier.padding(
//                                    start = 8.dp,
//                                    end = 8.dp,
//                                    top = 8.dp,
//                                    bottom = 8.dp
//                                )
//                            )
//
//                            Text(
//                                text = "게임",
//                                style = MaterialTheme.typography.titleLarge,
//                                modifier = Modifier
//                                    .padding(bottom = 6.dp)
//                            )
//
//                            Row(
//                                horizontalArrangement = Arrangement.SpaceAround,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                            ) {
//                                Row {
//                                    Text(
//                                        text = "컬링",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = userDataList.find { it.id == "firstGame" }?.value + "점",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = gameRankList[0] + "등",
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//
//                                Row {
//                                    Text(
//                                        text = "1to50",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//
//                                    val secondGameTime =
//                                        userDataList.find { it.id == "secondGame" }?.value
//
//                                    Text(
//                                        text = if (secondGameTime != "100000") {
//                                            secondGameTime
//                                        } else {
//                                            "-"
//                                        } + "초",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = gameRankList[1] + "등",
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//
//                            }
//
//                            Divider(
//                                color = Color.LightGray,
//                                thickness = 1.dp,
//                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
//                            )
//
//                            Text(
//                                text = "스도쿠",
//                                style = MaterialTheme.typography.titleMedium,
//                                modifier = Modifier
//                                    .padding(top = 8.dp, bottom = 6.dp)
//                            )
//
//                            Row(
//                                horizontalArrangement = Arrangement.SpaceAround,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                            ) {
//                                Row {
//                                    Text(
//                                        text = "쉬움",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = userDataList.find { it.id == "thirdGame" }?.value + "개",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = gameRankList[2] + "등",
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//
//                                Row {
//                                    Text(
//                                        text = "보통",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = userDataList.find { it.id == "thirdGame" }?.value2 + "개",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = gameRankList[3] + "등",
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//
//                                Row {
//                                    Text(
//                                        text = "어려움",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = userDataList.find { it.id == "thirdGame" }?.value3 + "개",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        modifier = Modifier
//                                            .padding(end = 6.dp)
//                                    )
//                                    Text(
//                                        text = gameRankList[4] + "등",
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//
//                            }
//
//                        }
//                    }
//
//                }
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceAround
//                ) {// 접속 정보
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "마을 탄생일",
//                            style = MaterialTheme.typography.labelMedium,
//                            modifier = Modifier
//                                .padding(end = 6.dp)
//                        )
//                        Text(
//                            text = userDataList.find { it.id == "date" }?.value3 ?: "2015-03-12",
//                            style = MaterialTheme.typography.labelMedium
//                        )
//                    }
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "접속일",
//                            style = MaterialTheme.typography.labelMedium,
//                            modifier = Modifier
//                                .padding(end = 6.dp)
//                        )
//                        Text(
//                            text = "${userDataList.find { it.id == "date" }?.value2 ?: "-"}일",
//                            style = MaterialTheme.typography.labelMedium
//                        )
//                    }
//                }
//
//            }
//
//            Row{
//
//                MainButton(
//                    text = if(page == 0) "상세 페이지 보기" else "메인 페이지 보기",
//                    onClick = {
//                        if(page == 0) page = 1 else page = 0
//                    },
//                    modifier = Modifier
//                )
//            }
//        }
//    }
}

@Preview(showBackground = true)
@Composable
fun NeighborInformationScreenPreview() {
    MypatTheme {
        NeighborInformationScreen(
            clickAllUserData = AllUser(firstDate = "1")
        )
    }
}