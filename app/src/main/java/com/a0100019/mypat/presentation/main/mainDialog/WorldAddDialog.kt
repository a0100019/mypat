package com.a0100019.mypat.presentation.main.mainDialog


import androidx.compose.foundation.background
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
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.ui.component.CuteIconButton
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
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AddDialogPatImage(
                                            patData = allPatDataList[index],
                                            onAddPatImageClick = { id ->
                                                // 여기에 id랑 type(pat) 활용하는 로직
                                                onAddPatClick(id)
                                            }
                                        )
                                        Text(allPatDataList[index].name)
                                        if (worldDataList.any { it.value == allPatDataList[index].id.toString() && it.type == "pat" }) {
                                            Text(
                                                text = worldDataList.indexOfFirst { it.value == allPatDataList[index].id.toString() && it.type == "pat" }.toString()
                                            )
                                        } else {
                                            Text(
                                                text = ""
                                            )
                                        }
                                    }
                                }
                            }
                            "item" -> {
                                items(allItemDataList.size) { index ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AddDialogItemImage(
                                            itemData = allItemDataList[index],
                                            onAddItemImageClick = { id ->
                                                // 여기에 id랑 type(pat) 활용하는 로직
                                                onAddItemClick(id)
                                            }
                                        )
                                        Text(allItemDataList[index].name)
                                        if (worldDataList.any { it.value == allItemDataList[index].id.toString() && it.type == "item"}) {
                                            Text(
                                                text = worldDataList.indexOfFirst { it.value == allItemDataList[index].id.toString() && it.type == "item" }.toString()
                                            )
                                        } else {
                                            Text(
                                                text = ""
                                            )
                                        }
                                    }
                                }
                            }
                            else -> {
                                items(allAreaDataList.size) { index ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AddDialogMapImage(
                                            areaData = allAreaDataList[index],
                                            onAddMapImageClick = onSelectMapImageClick
                                        )
                                        Text(allAreaDataList[index].name)
                                        if (mapWorldData.value == allAreaDataList[index].url) {
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

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    CuteIconButton(
                        text = " 다음 ",
                        onClick = onAddDialogChangeClick,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    CuteIconButton(
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