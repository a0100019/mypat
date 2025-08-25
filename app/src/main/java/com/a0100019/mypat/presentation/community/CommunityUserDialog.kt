package com.a0100019.mypat.presentation.community

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.item.WorldItemImage
import com.a0100019.mypat.presentation.ui.image.pat.PatInformationImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun CommunityUserDialog(
    onClose: () -> Unit,
    clickAllUserData: AllUser,
    clickAllUserWorldDataList: List<String> = emptyList(),
    patDataList: List<Pat> = emptyList(),
    itemDataList: List<Item> = emptyList(),
    onLikeClick: () -> Unit = {},
    onBanClick: () -> Unit = {},
    allUserDataList: List<AllUser> = emptyList(),
    allMapCount: String = "0"
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .fillMaxHeight(0.9f)
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
                    .fillMaxSize()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 이름, 좋아요
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 3.dp)
                    ,
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
                        .weight(1f)
                        .aspectRatio(1f / 1.25f)
                        .padding(start = 6.dp, end = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF8E7),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer),
//                    shadowElevation = 8.dp,
                ) {

                    JustImage(
                        filePath = clickAllUserData.area,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )

                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize()
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
                                patDataList.find { it.id.toString() == parts[0] }?.let { patData ->
                                    PatInformationImage(
                                        patUrl = patData.url,
                                        surfaceWidthDp = surfaceWidthDp,
                                        surfaceHeightDp = surfaceHeightDp,
                                        xFloat = parts[3].toFloat(),
                                        yFloat = parts[4].toFloat(),
                                        sizeFloat = parts[1].toFloat(),
                                        effect = parts[5].toInt()
                                    )
                                }

                            } else {
                                // item일 때 처리
                                itemDataList.find { it.id.toString() == parts[0] }?.let { itemData ->
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
                        .weight(0.6f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                        ,
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 2.dp,
                        color = MaterialTheme.colorScheme.scrim
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "도감",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(bottom = 6.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
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
                                        text = "${clickAllUserData.openItem}/${itemDataList.size}",
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

                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                        ,
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 2.dp,
                        color = MaterialTheme.colorScheme.scrim
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "게임",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(bottom = 6.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Row {
                                    Text(
                                        text = "슈팅",
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
                                        .indexOfFirst { it <= clickAllUserData.firstGame } + 1  // myScore보다 작거나 같은 첫 점수의 순위
                                    Text(
                                        text = firstGameRank.toString() + "등",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Row {
                                    Text(
                                        text = "블록",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .padding(end = 6.dp)
                                    )
                                    Text(
                                        text = clickAllUserData.secondGame + "점",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .padding(end = 6.dp)
                                    )
                                    val secondGameRank = allUserDataList
                                        .map { it.secondGame }        // 점수만 추출
                                        .sortedDescending()          // 높은 점수 순으로 정렬
                                        .indexOfFirst { it <= clickAllUserData.secondGame } + 1  // myScore보다 작거나 같은 첫 점수의 순위
                                    Text(
                                        text = secondGameRank.toString() + "등",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Divider(
                                color = Color.LightGray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            )

                            Text(
                                text = "스도쿠",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 6.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
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
                                        .indexOfFirst { it <= clickAllUserData.thirdGameEasy } + 1  // myScore보다 작거나 같은 첫 점수의 순위
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
                                        .indexOfFirst { it <= clickAllUserData.thirdGameNormal } + 1  // myScore보다 작거나 같은 첫 점수의 순위
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
                                        .indexOfFirst { it <= clickAllUserData.thirdGameHard } + 1  // myScore보다 작거나 같은 첫 점수의 순위
                                    Text(
                                        text = thirdGameHardRank.toString() + "등",
                                        style = MaterialTheme.typography.bodyMedium
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
                ) {

                    JustImage(
                        filePath = "etc/ban.png",
                        modifier = Modifier
                            .clickable {
                                onBanClick()
                            }
                            .size(15.dp)
                    )

//                    Row(
//                        modifier = Modifier
//                            .padding(8.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color(0xFFF9F3EA))  // 부드러운 배경색
//                            .clickable { onLikeClick() }
//                            .padding(horizontal = 16.dp, vertical = 12.dp)
//                        ,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        JustImage(
//                            filePath = "etc/arrow.png",
//                            modifier = Modifier
//                                .size(25.dp)
//                        )
//                        Spacer(modifier = Modifier.width(12.dp))  // 아이콘과 텍스트 사이 간격
//                        Text(
//                            text = "좋아요를 눌러주세요",
//                            style = MaterialTheme.typography.titleMedium,
//                            color = Color(0xFF333333)
//                        )
//                    }

                    MainButton(
                        text = "좋아요 누르기",
                        onClick = onLikeClick
                    )

                    MainButton(
                        text = "닫기",
                        onClick = onClose
                    )
                }

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
                firstDate = "2025-02-05",
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