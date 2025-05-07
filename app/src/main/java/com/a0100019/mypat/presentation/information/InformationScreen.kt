package com.a0100019.mypat.presentation.information

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
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
    ) {
        Row {
            Text(
                text = "이름"
            )
            Text(
                text = "#${userDataList.find { it.id == "name" }?.value2}"
            )
            Text(
                text = "좋아요 ${userDataList.find { it.id == "like" }?.value}개"
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth() // 가로 크기는 최대
                .aspectRatio(1 / 1.25f), // 세로가 가로의 1.25배
            contentAlignment = Alignment.Center // Center content
        ) {
            JustImage(
                filePath = mapUrl,
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

                itemDataList.map { itemData ->

                        WorldItemImage(
                            itemUrl = itemData.url,
                            surfaceWidthDp = surfaceWidthDp,
                            surfaceHeightDp = surfaceHeightDp,
                            xFloat = itemData.x,
                            yFloat = itemData.y,
                            sizeFloat = itemData.sizeFloat
                        )

                }

                patDataList.map { patData ->

                        PatInformationImage(
                            patUrl = patData.url,
                            surfaceWidthDp = surfaceWidthDp,
                            surfaceHeightDp = surfaceHeightDp,
                            xFloat = patData.x,
                            yFloat = patData.y,
                            sizeFloat = patData.sizeFloat,
                        )

                }


            }

        }

        Row {
            Text(
                text = "펫"
            )
            Text("${allPatDataList.count { it.date != "0" }}/${allPatDataList.size}")
        }

        Row {
            Text(
                text = "아이템"
            )
            Text("${allItemDataList.count { it.date != "0" }}/${allItemDataList.size}")
            Text(
                text = "맵"
            )
            Text("${allMapDataList.count { it.date != "0" }}/${allMapDataList.size}")
        }

        Row {
            Text(
                text = "컬링"
            )
            Text("${userDataList.find { it.id == "firstGame" }?.value}점")
            Text(
                text = "14등"
            )
        }

        Row {
            Text(
                text = "블록게임"
            )
            Text("${userDataList.find { it.id == "secondGame" }?.value}점")
            Text(
                text = "14등"
            )
        }

        Row {
            Text(
                text = "sudoku"
            )
            Text("${userDataList.find { it.id == "thirdGame" }?.value}점")
            Text(
                text = "14등"
            )
        }

        Row {
            Text(
                text = "최초 접속"
            )
            Text("${userDataList.find { it.id == "date" }?.value2}")

            Text(
                text = "접속일"
            )
            Text("${userDataList.find { it.id == "date" }?.value3}일")
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