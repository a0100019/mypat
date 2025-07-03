package com.a0100019.mypat.presentation.main.mainDialog


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.AddDialogMapImage
import com.a0100019.mypat.presentation.ui.image.item.AddDialogItemImage
import com.a0100019.mypat.presentation.ui.image.pat.AddDialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun WorldAddDialog(
    onClose: () -> Unit,
    allPatDataList: List<Pat>,
    allItemDataList: List<Item>,
    allAreaDataList: List<Area>,
    mapWorldData: World,
    onSelectMapImageClick: (String) -> Unit,
    onAddDialogChangeClick: () -> Unit,
    addDialogChange: String,
    worldDataList: List<World>,
    onAddPatClick: (String) -> Unit,
    onAddItemClick: (String) -> Unit,
    userDataList: List<User> = emptyList()
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = when(addDialogChange) {
                        "pat" -> "펫"
                        "item" -> "아이템"
                        else -> "맵"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                    )

                Text(
                    text = when(addDialogChange) {
                        "pat" -> "${userDataList.find { it.id == "pat" }?.value3} / ${userDataList.find { it.id == "pat" }?.value2}"
                        "item" -> "${userDataList.find { it.id == "item" }?.value3} / ${userDataList.find { it.id == "item" }?.value2}"
                        else -> ""
                    },
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4), // 한 줄에 5개씩 배치
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (addDialogChange) {
                            "pat" -> {
                                items(allPatDataList.size) { index ->
                                    val isSelected = worldDataList.any {
                                        it.value == allPatDataList[index].id.toString() && it.type == "pat"
                                    }

                                    val selectedOrder = worldDataList.indexOfFirst {
                                        it.value == allPatDataList[index].id.toString() && it.type == "pat"
                                    }.takeIf { it >= 0 }?.plus(1)?.toString() ?: ""

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .border(
                                                width = 2.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(4.dp)
                                    ) {
                                        AddDialogPatImage(
                                            patData = allPatDataList[index],
                                            onAddPatImageClick = { id ->
                                                onAddPatClick(id)
                                            }
                                        )
                                        Text(allPatDataList[index].name)
                                        Text(text = selectedOrder)
                                    }

                                }
                            }
                            "item" -> {
                                items(allItemDataList.size) { index ->
                                    val isSelected = worldDataList.any {
                                        it.value == allItemDataList[index].id.toString() && it.type == "item"
                                    }

                                    val selectedOrder = worldDataList.indexOfFirst {
                                        it.value == allItemDataList[index].id.toString() && it.type == "item"
                                    }.takeIf { it >= 0 }?.plus(1)?.toString() ?: ""

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .border(
                                                width = 2.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(4.dp)
                                    ) {
                                        AddDialogItemImage(
                                            itemData = allItemDataList[index],
                                            onAddItemImageClick = { id ->
                                                onAddItemClick(id)
                                            }
                                        )
                                        Text(allItemDataList[index].name)
                                        Text(text = selectedOrder)
                                    }

                                }
                            }
                            else -> {
                                items(allAreaDataList.size) { index ->

                                    val isSelected = mapWorldData.value == allAreaDataList[index].url

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .border(
                                                width = 2.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(4.dp) // 테두리 안쪽 여백
                                    ) {
                                        AddDialogMapImage(
                                            areaData = allAreaDataList[index],
                                            onAddMapImageClick = onSelectMapImageClick
                                        )
                                        Text(allAreaDataList[index].name)
                                        if (isSelected) {
                                            Text("선택")
                                        } else {
                                            Text("")
                                        }
                                    }


                                }
                            }
                        }
                    }
                }

                Text(
                    text = "숫자가 높을 수록 상단에 배치됩니다"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    MainButton(
                        text = " 다음 ",
                        onClick = onAddDialogChangeClick,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    MainButton(
                        text = " 확인 ",
                        onClick = onClose,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WorldAddDialogPreview() {
    MypatTheme {
        WorldAddDialog(
            onClose = {},
            allPatDataList = listOf(Pat(url = "pat/cat.json", name = "고양이"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json")),
            allItemDataList = listOf(Item(url = "item/table.png", name = "책상")),
            addDialogChange = "pat",
            onAddDialogChangeClick = {},
            allAreaDataList = listOf(Area()),
            mapWorldData = World(id = 1),
            onSelectMapImageClick = {},
            worldDataList = emptyList(),
            onAddItemClick = {},
            onAddPatClick = {}
        )
    }
}