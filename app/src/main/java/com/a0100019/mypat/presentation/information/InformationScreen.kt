package com.a0100019.mypat.presentation.information

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.item.WorldItemImage
import com.a0100019.mypat.presentation.ui.image.pat.PatInformationImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun InformationScreen(
    informationViewModel: InformationViewModel = hiltViewModel()

) {

    val informationState : InformationState = informationViewModel.collectAsState().value

    val context = LocalContext.current

    informationViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is InformationSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    InformationScreen(
        patDataList = informationState.patDataList,
        itemDataList = informationState.itemDataList,
        mapUrl = informationState.mapData?.value ?: "",
        allPatDataList = informationState.allPatDataList,
        allItemDataList = informationState.allItemDataList,
        allMapDataList = informationState.allMapDataList,
        userDataList = informationState.userData

        )
}



@Composable
fun InformationScreen(
    mapUrl : String,
    patDataList : List<Pat>,
    itemDataList : List<Item>,
    allPatDataList: List<Pat>,
    allItemDataList: List<Item>,
    allMapDataList: List<Item>,
    userDataList: List<User>

    ) {

    Surface (
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
        ,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFF8E7),
        border = BorderStroke(2.dp, Color(0xFF5A3A22)),
        shadowElevation = 8.dp,
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 이름, 좋아요
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${userDataList.find { it.id == "name" }?.value}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 6.dp)
                    )
                    Text(
                        text = "#${userDataList.find { it.id == "auth" }?.value2}",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Text(
                    text = "좋아요 ${userDataList.find { it.id == "community" }?.value}개",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(end = 10.dp)
                )
            }

            // 미니맵 뷰
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f / 1.25f)
                    .padding(start = 6.dp, end = 6.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFF8E7),
                border = BorderStroke(2.dp, Color(0xFF5A3A22)),
                shadowElevation = 8.dp,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    JustImage(
                        filePath = mapUrl,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )

                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val density = LocalDensity.current
                        val surfaceWidthDp = with(density) { constraints.maxWidth.toDp() }
                        val surfaceHeightDp = with(density) { constraints.maxHeight.toDp() }

                        itemDataList.forEach {
                            WorldItemImage(
                                itemUrl = it.url,
                                surfaceWidthDp = surfaceWidthDp,
                                surfaceHeightDp = surfaceHeightDp,
                                xFloat = it.x,
                                yFloat = it.y,
                                sizeFloat = it.sizeFloat
                            )
                        }

                        patDataList.forEach {
                            PatInformationImage(
                                patUrl = it.url,
                                surfaceWidthDp = surfaceWidthDp,
                                surfaceHeightDp = surfaceHeightDp,
                                xFloat = it.x,
                                yFloat = it.y,
                                sizeFloat = it.sizeFloat
                            )
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp,
                    color = Color(0xFFF9F3EA)
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
                                    text = "${allPatDataList.count { it.date != "0" }}/${allPatDataList.size}",
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
                                    text = "${allItemDataList.count { it.date != "0" }}/${allItemDataList.size}",
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
                                    text = "${allMapDataList.count { it.date != "0" }}/${allMapDataList.size}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp,
                    color = Color(0xFFF9F3EA)
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
                                    text = userDataList.find { it.id == "firstGame" }?.value ?: "0",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                )
                                Text(
                                    text = userDataList.find { it.id == "firstGame" }?.value ?: "0",
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
                                    text = userDataList.find { it.id == "secondGame" }?.value
                                        ?: "0",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                )
                                Text(
                                    text = userDataList.find { it.id == "firstGame" }?.value ?: "0",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                        }

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
                                    text = userDataList.find { it.id == "thirdGame" }?.value ?: "0",
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
                                    text = userDataList.find { it.id == "thirdGame" }?.value2
                                        ?: "0",
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
                                    text = userDataList.find { it.id == "thirdGame" }?.value3
                                        ?: "0",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                        }

                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {// 접속 정보
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "마을 탄생일",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(end = 6.dp)
                    )
                    Text(
                        text = userDataList.find { it.id == "date" }?.value3 ?: "2015-03-12",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "접속일",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(end = 6.dp)
                    )
                    Text(
                        text = "${userDataList.find { it.id == "date" }?.value2 ?: "-"}일",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InformationScreenPreview() {
    MypatTheme {
        InformationScreen(
            mapUrl = "map/beach.jpg",
            patDataList = listOf(Pat(url = "pat/cat.json")),
            itemDataList = listOf(Item(url = "item/table.png")),
            allPatDataList = listOf(Pat(url = "pat/cat.json")),
            allItemDataList = listOf(Item(url = "item/table.png")),
            allMapDataList = listOf(Item(url = "item/forest.png")),
            userDataList = listOf(User(id = "firstGame"))
        )
    }
}