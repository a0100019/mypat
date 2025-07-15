package com.a0100019.mypat.presentation.index

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.component.MainButton
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
        allAreaDataList = indexState.allAreaDataList,

        onTypeChangeClick = indexViewModel::onTypeChangeClick,
        onCloseDialog = indexViewModel::onCloseDialog,
        onCardClick = indexViewModel::onCardClick,

        typeChange = indexState.typeChange,
        dialogPatIndex = indexState.dialogPatIndex,
        dialogItemIndex = indexState.dialogItemIndex,
        dialogAreaIndex = indexState.dialogAreaIndex
    )
}



@Composable
fun IndexScreen(
    allPatDataList: List<Pat>,
    allItemDataList: List<Item>,
    allAreaDataList: List<Area>,

    onTypeChangeClick: (String) -> Unit,
    onCloseDialog: () -> Unit,
    onCardClick: (Int) -> Unit,

    typeChange: String,
    dialogPatIndex: Int,
    dialogItemIndex: Int,
    dialogAreaIndex: Int
) {

    // 다이얼로그 표시
    if (dialogPatIndex != -1 && typeChange == "pat") {
        IndexPatDialog(
            onClose = onCloseDialog,
            open = allPatDataList.getOrNull(dialogPatIndex)!!.date != "0",
            patData = allPatDataList.getOrNull(dialogPatIndex)!!,
        )
    } else if(dialogItemIndex != -1 && typeChange == "item") {
        IndexItemDialog(
            onClose = onCloseDialog,
//            open =
            itemData = allItemDataList.getOrNull(dialogItemIndex)!!
        )
    } else if(dialogAreaIndex != -1 && typeChange == "area") {
        IndexAreaDialog(
            onClose = onCloseDialog,
            areaData = allAreaDataList.getOrNull(dialogAreaIndex)!!
        )
    }


    // Fullscreen container
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
    ) {
        Text(
            text = "도감",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            when (typeChange) {
                "pat" -> {
                    Text(
                        text = "펫",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(12.dp)
                    )
                    Text(
                        text = "${allPatDataList.count { it.date != "0"}}/${allPatDataList.size}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(12.dp)
                    )
                }
                "item" -> {
                    Text(
                        text = "아이템",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(12.dp)
                    )
                    Text(
                        text = "${allItemDataList.count {it.date != "0"}}/${allItemDataList.size}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(12.dp)
                    )
                }
                else -> {
                    Text(
                        text = "맵",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(12.dp)
                    )
                    Text(
                        text = "${allAreaDataList.count {it.date != "0"}}/${allAreaDataList.size}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(12.dp)
                    )
                }
            }

        }

        when (typeChange) {
            "pat" -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(allPatDataList.size) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .aspectRatio(0.7f),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            onClick = { onCardClick(index) }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {

                                // 전체 컨텐츠
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // 🐾 이미지 박스
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .background(
                                                color = if(allPatDataList[index].date != "0") { Color(0xFFFFF9C4) } else {Color.LightGray},
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                        ,
                                        contentAlignment = Alignment.Center
                                    ) {
                                        DialogPatImage(allPatDataList[index].url)
                                    }

                                    // 📝 이름
                                    Text(
                                        text = allPatDataList[index].name,
                                        modifier = Modifier
                                            .padding(top = 10.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }

                                // 🔒 잠금 아이콘
                                if (allPatDataList[index].date == "0") {
                                    JustImage(
                                        filePath = "etc/lock.png",
                                        modifier = Modifier
                                            .size(35.dp)
                                            .align(Alignment.TopStart)
                                            .padding(8.dp)
                                    )
                                }

                            }
                        }

                    }
                }
            }
            "item" -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(allItemDataList.size) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp)
                                .aspectRatio(0.7f), // 카드 비율 설정
                            elevation = CardDefaults.cardElevation(4.dp),
                            onClick = { onCardClick(index)}
                        ) {

                            Box{
                                if(allItemDataList[index].date == "0") {
                                    JustImage(
                                        filePath = "etc/lock.png",
                                        modifier = Modifier
                                            .size(30.dp) // 부모의 20% 크기
                                            .aspectRatio(1f)
                                            .padding(5.dp)
                                    )
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
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(allAreaDataList.size) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp)
                                .aspectRatio(0.7f), // 카드 비율 설정
                            elevation = CardDefaults.cardElevation(4.dp),
                            onClick = { onCardClick(index)}
                        ) {

                            Box {
                                if(allAreaDataList[index].date == "0") {
                                    JustImage(
                                        filePath = "etc/lock.png",
                                        modifier = Modifier
                                            .size(30.dp) // 부모의 20% 크기
                                            .aspectRatio(1f)
                                            .padding(5.dp)
                                    )
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
                                        ItemImage(allAreaDataList[index].url)
                                    }

                                    // 텍스트
                                    Text(
                                        text = allAreaDataList[index].name,
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
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            val types = listOf("pat" to "펫", "item" to "아이템", "area" to "맵")

            types.forEach { (type, label) ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    MainButton(
                        onClick = { onTypeChangeClick(type) },
                        text = label,
                        modifier = Modifier.fillMaxWidth(),
                        iconResId = if (typeChange == type) R.drawable.check else null,
                        imageSize = 18.dp
                    )
                }
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun IndexScreenPreview() {
    MypatTheme {
        IndexScreen(
            allPatDataList = listOf(Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json")),
            allItemDataList = listOf(Item(url = "item/table.png")),
            allAreaDataList = listOf(Area(url = "area/forest.png")),
            onTypeChangeClick = {},
            typeChange = "pat",
            dialogPatIndex = -1,
            onCloseDialog = {},
            onCardClick = {},
            dialogItemIndex = -1,
            dialogAreaIndex = -1,
        )
    }
}