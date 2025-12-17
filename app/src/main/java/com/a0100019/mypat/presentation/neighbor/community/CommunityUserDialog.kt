package com.a0100019.mypat.presentation.neighbor.community

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.information.medalName
import com.a0100019.mypat.presentation.ui.MusicPlayer
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.component.TextAutoResizeSingleLine
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.item.WorldItemImage
import com.a0100019.mypat.presentation.ui.image.pat.PatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun CommunityUserDialog(
    onClose: () -> Unit,
    clickAllUserData: AllUser = AllUser(),
    clickAllUserWorldDataList: List<String> = emptyList(),
    patDataList: List<Pat> = emptyList(),
    itemDataList: List<Item> = emptyList(),
    onLikeClick: () -> Unit = {},
    onBanClick: () -> Unit = {},
    allUserDataList: List<AllUser> = emptyList(),
    allMapCount: String = "0",
    onPrivateChatClick: () -> Unit = {},
) {

    var page by remember { mutableIntStateOf(1) }

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
                                onClick = onPrivateChatClick
                            )

                            Spacer(modifier = Modifier.size(60.dp))

                            MainButton(
                                text = "닫기",
                                onClick = onClose
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

        Dialog(
            onDismissRequest = onClose
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(340.dp)
                        .fillMaxHeight(0.8f)
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
                    if (page == 0) {
                        Column(
                            modifier = Modifier
//                    .fillMaxSize()
                            ,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

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

                            Text(
                                text = "칭호"
                            )

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f / 1.25f)
                                    .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFFF8E7),
                                border = BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.primaryContainer
                                ),
//                    shadowElevation = 8.dp,
                            ) {

                                JustImage(
                                    filePath = clickAllUserData.area,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds
                                )

                                BoxWithConstraints(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    val density = LocalDensity.current

                                    // Surface 크기 가져오기 (px → dp 변환)
                                    val surfaceWidth = constraints.maxWidth
                                    val surfaceHeight = constraints.maxHeight

                                    val surfaceWidthDp = with(density) { surfaceWidth.toDp() }
                                    val surfaceHeightDp = with(density) { surfaceHeight.toDp() }

                                    clickAllUserWorldDataList.forEach { data ->
                                        val parts = data.split("@")
                                        if (parts[2] == "pat") {
                                            // pat일 때 처리
                                            patDataList.find { it.id.toString() == parts[0] }
                                                ?.let { patData ->

                                                    PatImage(
                                                        patUrl = patData.url,
                                                        surfaceWidthDp = surfaceWidthDp,
                                                        surfaceHeightDp = surfaceHeightDp,
                                                        xFloat = parts[3].toFloat(),
                                                        yFloat = parts[4].toFloat(),
                                                        sizeFloat = parts[1].toFloat(),
                                                        effect = parts[5].toInt(),
                                                        onClick = { }
                                                    )
                                                }

                                        } else {
                                            // item일 때 처리
                                            itemDataList.find { it.id.toString() == parts[0] }
                                                ?.let { itemData ->
                                                    WorldItemImage(
                                                        itemUrl = itemData.url,
                                                        surfaceWidthDp = surfaceWidthDp,
                                                        surfaceHeightDp = surfaceHeightDp,
                                                        xFloat = parts[3].toFloat(),
                                                        yFloat = parts[4].toFloat(),
                                                        sizeFloat = parts[1].toFloat(),
                                                    )
                                                }
                                        }

                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    shape = RoundedCornerShape(16.dp),
                                    tonalElevation = 2.dp,
                                    color = MaterialTheme.colorScheme.scrim
                                ) {

                                    Column(
                                        modifier = Modifier
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = introduction,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    }

                                }
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
                                            onBanClick()
                                        }
                                        .size(15.dp)
                                )

                                MainButton(
                                    text = "좋아요 누르기",
                                    onClick = onLikeClick
                                )

                                MainButton(
                                    text = "1대1 채팅하기",
                                    onClick = onPrivateChatClick
                                )

                                MainButton(
                                    text = "닫기",
                                    onClick = onClose
                                )
                            }

                        }
                    } else {
                        // 상세 페이지 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
                        Column(
                            modifier = Modifier
//                    .fillMaxSize()
                            ,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

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

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f / 1.25f)
                                    .padding(6.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFFF8E7),
                                border = BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.primaryContainer
                                ),
                            ) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3), // 한 줄에 3개
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    //칭호개수 +1 만큼 아이템크기
                                    items(16) { index ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.outline,
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            TextAutoResizeSingleLine(
                                                text = medalName(index + 1),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )
                                            if (medalList.contains(index + 1)) {
                                                Text(
                                                    text = "획득",
                                                    style = MaterialTheme.typography.titleMedium,
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    shape = RoundedCornerShape(16.dp),
                                    tonalElevation = 2.dp,
                                    color = MaterialTheme.colorScheme.scrim
                                ) {

                                    Column(
                                        modifier = Modifier
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Text(
                                                text = "도감",
                                                style = MaterialTheme.typography.titleLarge,
                                                modifier = Modifier
                                            )

                                            Row {
                                                Text(
                                                    text = "펫",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                Text(
                                                    text = "${clickAllUserData.openPat}/${patDataList.size}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                            Row {
                                                Text(
                                                    text = "아이템",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                Text(
                                                    text = "${clickAllUserData.openItem.toInt() - 20}/${itemDataList.size - 20}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                            Row {
                                                Text(
                                                    text = "맵",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                Text(
                                                    text = "${clickAllUserData.openArea}/${allMapCount}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }

                                        Divider(
                                            color = Color.LightGray,
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(
                                                start = 2.dp,
                                                end = 2.dp,
                                                top = 4.dp,
                                                bottom = 4.dp
                                            )
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Text(
                                                text = "게임",
                                                style = MaterialTheme.typography.titleLarge,
                                                modifier = Modifier
                                            )

                                            Row {
                                                Text(
                                                    text = "컬링",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                Text(
                                                    text = clickAllUserData.firstGame + "점",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                val firstGameRank = allUserDataList
                                                    .map { it.firstGame }        // 점수만 추출
                                                    .sortedDescending()          // 높은 점수 순으로 정렬
                                                    .count { it.toInt() > clickAllUserData.firstGame.toInt() } + 1  // myScore보다 작거나 같은 첫 점수의 순위
                                                Text(
                                                    text = firstGameRank.toString() + "등",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                            Row {
                                                Text(
                                                    text = "1to50",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )

                                                val secondGameTime = clickAllUserData.secondGame

                                                Text(
                                                    text = if (secondGameTime != "100000") {
                                                        secondGameTime
                                                    } else {
                                                        "-"
                                                    } + "초",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                val secondGameRank = allUserDataList
                                                    .map { it.secondGame }        // 점수만 추출
                                                    .sortedDescending()          // 높은 점수 순으로 정렬
                                                    .count { it.toDouble() < clickAllUserData.secondGame.toDouble() } + 1  // myScore보다 작거나 같은 첫 점수의 순위
                                                Text(
                                                    text = secondGameRank.toString() + "등",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Text(
                                                text = "스도쿠",
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier
                                            )

                                            Row {
                                                Text(
                                                    text = "쉬움",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                Text(
                                                    text = clickAllUserData.thirdGameEasy + "개",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                val thirdGameEasyRank = allUserDataList
                                                    .map { it.thirdGameEasy }        // 점수만 추출
                                                    .sortedDescending()          // 높은 점수 순으로 정렬
                                                    .count { it.toInt() > clickAllUserData.thirdGameEasy.toInt() } + 1  // myScore보다 작거나 같은 첫 점수의 순위
                                                Text(
                                                    text = thirdGameEasyRank.toString() + "등",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                            Row {
                                                Text(
                                                    text = "보통",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                Text(
                                                    text = clickAllUserData.thirdGameNormal + "개",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                val thirdGameNormalRank = allUserDataList
                                                    .map { it.thirdGameNormal }        // 점수만 추출
                                                    .sortedDescending()          // 높은 점수 순으로 정렬
                                                    .count { it.toInt() > clickAllUserData.thirdGameNormal.toInt() } + 1  // myScore보다 작거나 같은 첫 점수의 순위
                                                Text(
                                                    text = thirdGameNormalRank.toString() + "등",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                            Row {
                                                Text(
                                                    text = "어려움",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                Text(
                                                    text = clickAllUserData.thirdGameHard + "개",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                val thirdGameHardRank = allUserDataList
                                                    .map { it.thirdGameHard }        // 점수만 추출
                                                    .sortedDescending()          // 높은 점수 순으로 정렬
                                                    .count { it.toInt() > clickAllUserData.thirdGameHard.toInt() } + 1  // myScore보다 작거나 같은 첫 점수의 순위
                                                Text(
                                                    text = thirdGameHardRank.toString() + "등",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                        }

                                        Divider(
                                            color = Color.LightGray,
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(
                                                start = 2.dp,
                                                end = 2.dp,
                                                top = 4.dp,
                                                bottom = 4.dp
                                            )
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Row {
                                                Text(
                                                    text = "마을 탄생일",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                Text(
                                                    text = clickAllUserData.firstDate,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                )
                                            }

                                            Row {
                                                Text(
                                                    text = "접속일",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                        .padding(end = 6.dp)
                                                )
                                                Text(
                                                    text = "${clickAllUserData.totalDate}일",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier
                                                )
                                            }

                                        }

                                    }

                                }
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
                                            onBanClick()
                                        }
                                        .size(15.dp)
                                )

                                MainButton(
                                    text = "좋아요 누르기",
                                    onClick = onLikeClick
                                )

                                MainButton(
                                    text = "1대1 채팅하기",
                                    onClick = onPrivateChatClick
                                )

                                MainButton(
                                    text = "닫기",
                                    onClick = onClose
                                )
                            }

                        }
                    }
                }
                MainButton(
                    text = if (page == 0) "상세 페이지 보기" else "메인 페이지 보기",
                    onClick = {
                        if (page == 0) page = 1 else page = 0
                    },
                    modifier = Modifier
                        .padding(top = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityUserDialogPreview() {
    MypatTheme {
        CommunityUserDialog(
            onClose = {},
            AllUser(
                tag = "22",
                lastLogin = 342112,
                ban = "0",
                like = "54",
                warning = "0",
                firstDate = "0",
                openItem = "30",
                area = "area/forest.jpg",
                name = "이222유빈",
                openPat = "20",
                totalDate = "134",
                worldData = "1@0.2@pat@0.25@0.69/2@0.2@pat@0.25@0.569/1@0.2@pat@0.125@0.69/1@0.2@item@0.25@0.69/2@0.2@item@0.125@0.769/1@0.2@item@0.225@0.1691@0.2@pat@0.25@0.669/2@0.2@pat@0.25@0.369/2@0.3@pat@0.325@0.69/1@0.2@pat@0.725@0.769/1@0.2@item@0.425@0.669",
            ),
        )
    }
}