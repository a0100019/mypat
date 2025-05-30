package com.a0100019.mypat.presentation.index

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.item.ItemImage
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun IndexScreen(
    indexViewModel: IndexViewModel = hiltViewModel()

) {

    val indexState : IndexState = indexViewModel.collectAsState().value

    val context = LocalContext.current

    indexViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is IndexSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    IndexScreen(
        allPatDataList = indexState.allPatDataList,
        allItemDataList = indexState.allItemDataList,
        allMapDataList = indexState.allMapDataList,

        onTypeChangeClick = indexViewModel::onTypeChangeClick,
        onCloseDialog = indexViewModel::onCloseDialog,
        onCardClick = indexViewModel::onCardClick,

        typeChange = indexState.typeChange,
        dialogPatIndex = indexState.dialogPatIndex,
        dialogItemIndex = indexState.dialogItemIndex,
        dialogMapIndex = indexState.dialogMapIndex
    )
}



@Composable
fun IndexScreen(
    allPatDataList: List<Pat>,
    allItemDataList: List<Item>,
    allMapDataList: List<Item>,

    onTypeChangeClick: (String) -> Unit,
    onCloseDialog: () -> Unit,
    onCardClick: (Int) -> Unit,

    typeChange: String,
    dialogPatIndex: Int,
    dialogItemIndex: Int,
    dialogMapIndex: Int
) {

    // 다이얼로그 표시
    if (dialogPatIndex != -1 && typeChange == "pat") {
        IndexPatDialog(
            onClose = onCloseDialog,
            patData = allPatDataList.getOrNull(dialogPatIndex)!!,
        )
    } else if(dialogItemIndex != -1 && typeChange == "item") {
        IndexItemDialog(
            onClose = onCloseDialog,
            itemData = allItemDataList.getOrNull(dialogItemIndex)!!
        )
    } else if(dialogMapIndex != -1 && typeChange == "map") {
        IndexMapDialog(
            onClose = onCloseDialog,
            mapData = allMapDataList.getOrNull(dialogMapIndex)!!
        )
    }


    // Fullscreen container
    Column(modifier = Modifier.fillMaxSize()) {
        Text("도감")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(typeChange)
            when (typeChange) {
                "pat" -> {
                    Text("${allPatDataList.count { it.date != "0"}}/${allPatDataList.size}")
                }
                "item" -> {
                    Text("${allItemDataList.count {it.date != "0"}}/${allItemDataList.size}")
                }
                else -> {
                    Text("${allMapDataList.count {it.date != "0"}}/${allMapDataList.size}")
                }
            }

        }

        when (typeChange) {
            "pat" -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4), // 한 줄에 5개씩 배치
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allPatDataList.size) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.7f), // 카드 비율 설정
                            elevation = CardDefaults.cardElevation(4.dp),
                            onClick = { onCardClick(index)}
                        ) {
                            if(allPatDataList[index].date == "0") {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp) // 부모의 20% 크기
                                        .aspectRatio(1f)
                                        .padding(5.dp)
                                ) {
                                    JustImage("etc/lock.png")
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp), // 카드 안쪽 여백
                                verticalArrangement = Arrangement.SpaceBetween, // 이미지와 텍스트를 상하로 배치
                                horizontalAlignment = Alignment.CenterHorizontally // 가로 중앙 정렬
                            ) {
                                // 이미지
                                Box(
                                    modifier = Modifier
                                        .weight(1f) // 이미지가 최대한 공간을 차지하도록
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    DialogPatImage(allPatDataList[index].url)
                                }

                                // 텍스트
                                Text(
                                    text = allPatDataList[index].name,
                                    modifier = Modifier
                                        .padding(top = 8.dp) // 이미지와의 간격 설정
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center, // 텍스트 중앙 정렬
                                    style = MaterialTheme.typography.bodySmall // 텍스트 스타일 적용
                                )
                            }
                        }
                    }
                }
            }
            "item" -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allItemDataList.size) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.7f), // 카드 비율 설정
                            elevation = CardDefaults.cardElevation(4.dp),
                            onClick = { onCardClick(index)}
                        ) {
                            if(allItemDataList[index].date == "0") {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp) // 부모의 20% 크기
                                        .aspectRatio(1f)
                                        .padding(5.dp)
                                ) {
                                    JustImage("etc/lock.png")
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp), // 카드 안쪽 여백
                                verticalArrangement = Arrangement.SpaceBetween, // 이미지와 텍스트를 상하로 배치
                                horizontalAlignment = Alignment.CenterHorizontally // 가로 중앙 정렬
                            ) {
                                // 이미지
                                Box(
                                    modifier = Modifier
                                        .weight(1f) // 이미지가 최대한 공간을 차지하도록
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ItemImage(allItemDataList[index].url)
                                }

                                // 텍스트
                                Text(
                                    text = allItemDataList[index].name,
                                    modifier = Modifier
                                        .padding(top = 8.dp) // 이미지와의 간격 설정
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center, // 텍스트 중앙 정렬
                                    style = MaterialTheme.typography.bodySmall // 텍스트 스타일 적용
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // 한 줄에 5개씩 배치
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allMapDataList.size) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.7f), // 카드 비율 설정
                            elevation = CardDefaults.cardElevation(4.dp),
                            onClick = { onCardClick(index)}
                        ) {
                            if(allMapDataList[index].date == "0") {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp) // 부모의 20% 크기
                                        .aspectRatio(1f)
                                        .padding(5.dp)
                                ) {
                                    JustImage("etc/lock.png")
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp), // 카드 안쪽 여백
                                verticalArrangement = Arrangement.SpaceBetween, // 이미지와 텍스트를 상하로 배치
                                horizontalAlignment = Alignment.CenterHorizontally // 가로 중앙 정렬
                            ) {
                                // 이미지
                                Box(
                                    modifier = Modifier
                                        .weight(1f) // 이미지가 최대한 공간을 차지하도록
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ItemImage(allMapDataList[index].url)
                                }

                                // 텍스트
                                Text(
                                    text = allMapDataList[index].name,
                                    modifier = Modifier
                                        .padding(top = 8.dp) // 이미지와의 간격 설정
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center, // 텍스트 중앙 정렬
                                    style = MaterialTheme.typography.bodySmall // 텍스트 스타일 적용
                                )
                            }
                        }
                    }
                }
            }
        }

            Row {
                Button(
                    onClick = { onTypeChangeClick("pat") }
                ) {
                    Text("pat")
                }

                Button(
                    onClick = { onTypeChangeClick("item") }
                ) {
                    Text("item")
                }


                Button(
                    onClick = { onTypeChangeClick("map") }
                ) {
                    Text("map")
                }


            }


    }
}

@Preview(showBackground = true)
@Composable
fun IndexScreenPreview() {
    MypatTheme {
        IndexScreen(
            allPatDataList = listOf(Pat(url = "pat/cat.json")),
            allItemDataList = listOf(Item(url = "item/table.png")),
            allMapDataList = listOf(Item(url = "item/forest.png")),
            onTypeChangeClick = {},
            typeChange = "pat",
            dialogPatIndex = -1,
            onCloseDialog = {},
            onCardClick = {},
            dialogItemIndex = -1,
            dialogMapIndex = -1,
        )
    }
}