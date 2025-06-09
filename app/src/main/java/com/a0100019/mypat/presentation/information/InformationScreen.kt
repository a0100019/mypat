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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
                ,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 이름, 좋아요
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("이름", style = MaterialTheme.typography.labelMedium)
            Text("#${userDataList.find { it.id == "name" }?.value2}")
            Text("좋아요 ${userDataList.find { it.id == "like" }?.value}개")
        }

        // 미니맵 뷰
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1.25f),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFFF8E7),
            border = BorderStroke(2.dp, Color(0xFF5A3A22)),
            shadowElevation = 8.dp
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

        // 수집 정보 (펫, 아이템, 맵)
        InfoCard(label = "펫", value = "${allPatDataList.count { it.date != "0" }}/${allPatDataList.size}")
        InfoCard(label = "아이템", value = "${allItemDataList.count { it.date != "0" }}/${allItemDataList.size}")
        InfoCard(label = "맵", value = "${allMapDataList.count { it.date != "0" }}/${allMapDataList.size}")

        // 게임 점수
        GameScoreRow("컬링", userDataList.find { it.id == "firstGame" }?.value ?: "0", "14등")
        GameScoreRow("블록게임", userDataList.find { it.id == "secondGame" }?.value ?: "0", "14등")
        GameScoreRow("sudoku", userDataList.find { it.id == "thirdGame" }?.value ?: "0", "14등")

        // 접속 정보
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("최초 접속", style = MaterialTheme.typography.labelMedium)
            Text(userDataList.find { it.id == "date" }?.value2 ?: "-")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("접속일", style = MaterialTheme.typography.labelMedium)
            Text("${userDataList.find { it.id == "date" }?.value3 ?: "-"}일")
        }
    }
}

@Composable
fun InfoCard(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        color = Color(0xFFF9F3EA)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun GameScoreRow(gameName: String, score: String, rank: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(gameName, style = MaterialTheme.typography.labelMedium)
        Text("$score 점")
        Text(rank)
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