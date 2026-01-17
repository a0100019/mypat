package com.a0100019.mypat.presentation.main.mainDialog


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.ui.component.TextAutoResizeSingleLine
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.AddDialogMapImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.item.AddDialogItemImage
import com.a0100019.mypat.presentation.ui.image.pat.AddDialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun WorldAddDialog(
    onClose: () -> Unit,
    allPatDataList: List<Pat>,
    allItemDataList: List<Item>,
    allAreaDataList: List<Area>,
    allShadowDataList: List<Item> = emptyList(),
    mapWorldData: World,
    onSelectMapImageClick: (String) -> Unit,
    onAddDialogChangeClick: (String) -> Unit,
    addDialogChange: String,
    worldDataList: List<World>,
    onAddPatClick: (String) -> Unit,
    onAddItemClick: (String) -> Unit,
    userDataList: List<User> = emptyList(),
    onAddShadowClick: (String) -> Unit = {},
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .fillMaxHeight(0.8f)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline, // 테두리
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.background, // 배경색
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
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
                        .background(
                            MaterialTheme.colorScheme.scrim,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primaryContainer, // 테두리
                            shape = RoundedCornerShape(16.dp)
                        )
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
                                            .fillMaxWidth()
                                            .height(120.dp) // --- [포인트 1] 전체 높이를 고정하여 위아래 길이를 통일 ---
                                            .border(
                                                width = 2.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(4.dp)
                                    ) {
                                        // 이미지 영역 (이미지 크기가 일정하다고 가정)
                                        AddDialogPatImage(
                                            patData = allPatDataList[index],
                                            onAddPatImageClick = { id ->
                                                onAddPatClick(id)
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // --- [포인트 2] 이름 영역의 높이를 고정 (글자수가 달라도 영역 유지) ---
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            TextAutoResizeSingleLine(
                                                text = allPatDataList[index].name,
                                            )
                                        }

                                        // --- [포인트 3] 순서 텍스트 영역 고정 ---
                                        Box(
                                            modifier = Modifier.height(20.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = selectedOrder,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
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
                                            .fillMaxWidth()
                                            .height(120.dp) // --- [포인트 1] 전체 높이를 동일하게 고정 ---
                                            .border(
                                                width = 2.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(4.dp)
                                    ) {
                                        // 이미지 영역
                                        AddDialogItemImage(
                                            itemData = allItemDataList[index],
                                            onAddItemImageClick = { id ->
                                                onAddItemClick(id)
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // --- [포인트 2] 이름 영역 높이 고정 (글자 수 영향 방지) ---
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            TextAutoResizeSingleLine(
                                                text = allItemDataList[index].name,
                                            )
                                        }

                                        // --- [포인트 3] 선택 순서 텍스트 영역 고정 ---
                                        Box(
                                            modifier = Modifier.height(20.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = selectedOrder,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                                // 🔹 Text를 한 줄 전체(span = 4) 차지하도록
//                                item(span = { GridItemSpan(maxLineSpan) }) {
//                                    Text(
//                                        text = "그림자는 자연스러운 배치를 위한 아이템으로 공간을 차지하지 않습니다. 자유롭게 사용하세요",
//                                        modifier = Modifier.fillMaxWidth(),
//                                        textAlign = TextAlign.Center
//                                    )
//                                }
//                                items(allShadowDataList.size) { index ->
//                                    val isSelected = worldDataList.any {
//                                        it.value == allShadowDataList[index].id.toString() && it.type == "item"
//                                    }
//
//                                    val selectedOrder = worldDataList.indexOfFirst {
//                                        it.value == allShadowDataList[index].id.toString() && it.type == "item"
//                                    }.takeIf { it >= 0 }?.plus(1)?.toString() ?: ""
//
//                                    Column(
//                                        horizontalAlignment = Alignment.CenterHorizontally,
//                                        modifier = Modifier
//                                            .border(
//                                                width = 2.dp,
//                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
//                                                shape = RoundedCornerShape(8.dp)
//                                            )
//                                            .padding(4.dp)
//                                    ) {
//                                        AddDialogItemImage(
//                                            itemData = allShadowDataList[index],
//                                            onAddItemImageClick = { id ->
//                                                onAddShadowClick(id)
//                                            }
//                                        )
//                                        TextAutoResizeSingleLine(
//                                            text = allShadowDataList[index].name,
//                                        )
//                                        Text(text = selectedOrder)
//                                    }
//
//                                }
                            }
                            else -> {
                                items(allAreaDataList.size) { index ->

                                    val isSelected = mapWorldData.value == allAreaDataList[index].url

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp) // --- [포인트 1] 전체 높이를 120.dp로 통일 ---
                                            .border(
                                                width = 2.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(4.dp)
                                    ) {
                                        // 이미지 영역
                                        AddDialogMapImage(
                                            areaData = allAreaDataList[index],
                                            onAddMapImageClick = onSelectMapImageClick
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // --- [포인트 2] 이름 영역 높이 고정 (32.dp) ---
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            TextAutoResizeSingleLine(
                                                text = allAreaDataList[index].name,
                                            )
                                        }

                                        // --- [포인트 3] 선택 상태 표시 영역 고정 (20.dp) ---
                                        Box(
                                            modifier = Modifier.height(20.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isSelected) "선택" else "",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if(addDialogChange != "area"){
                    Text(
                        text = "숫자가 높을 수록 앞에 배치됩니다"
                    )
                } else {
                    Text(
                        text = "맵에 따라 배경 음악이 변경됩니다"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // --- 1. Pat (동물/펫) 버튼 ---
                    val isPatSelected = addDialogChange == "pat"
                    val interactionPat = remember { MutableInteractionSource() }
                    val isPressedPat by interactionPat.collectIsPressedAsState()
                    val scalePat by animateFloatAsState(if (isPressedPat) 0.9f else 1f, label = "")

                    Box(
                        modifier = Modifier
                            .graphicsLayer { scaleX = scalePat; scaleY = scalePat }
                            .size(52.dp)
                            .background(
                                color = if (isPatSelected) Color(0xFFE8F5E9) else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = if (isPatSelected) 2.dp else 0.dp,
                                color = if (isPatSelected) Color(0xFF4CAF50).copy(0.3f) else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = interactionPat,
                                indication = null,
                                onClick = { onAddDialogChangeClick("pat") }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        JustImage(
                            filePath = "etc/worldPat.png",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // --- 2. Item (아이템) 버튼 ---
                    val isItemSelected = addDialogChange == "item"
                    val interactionItem = remember { MutableInteractionSource() }
                    val isPressedItem by interactionItem.collectIsPressedAsState()
                    val scaleItem by animateFloatAsState(if (isPressedItem) 0.9f else 1f, label = "")

                    Box(
                        modifier = Modifier
                            .graphicsLayer { scaleX = scaleItem; scaleY = scaleItem }
                            .size(52.dp)
                            .background(
                                color = if (isItemSelected) Color(0xFFFFF3E0) else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = if (isItemSelected) 2.dp else 0.dp,
                                color = if (isItemSelected) Color(0xFFFF9800).copy(0.3f) else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = interactionItem,
                                indication = null,
                                onClick = { onAddDialogChangeClick("item") }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        JustImage(
                            filePath = "etc/worldItem.png",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // --- 3. Area (지역) 버튼 ---
                    val isAreaSelected = addDialogChange == "area"
                    val interactionArea = remember { MutableInteractionSource() }
                    val isPressedArea by interactionArea.collectIsPressedAsState()
                    val scaleArea by animateFloatAsState(if (isPressedArea) 0.9f else 1f, label = "")

                    Box(
                        modifier = Modifier
                            .graphicsLayer { scaleX = scaleArea; scaleY = scaleArea }
                            .size(52.dp)
                            .background(
                                color = if (isAreaSelected) Color(0xFFF3E5F5) else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = if (isAreaSelected) 2.dp else 0.dp,
                                color = if (isAreaSelected) Color(0xFF9C27B0).copy(0.3f) else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = interactionArea,
                                indication = null,
                                onClick = { onAddDialogChangeClick("area") }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        JustImage(
                            filePath = "etc/worldArea.png",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // --- 확인 버튼 ---
                    MainButton(
                        text = " 확인 ",
                        onClick = onClose,
                        modifier = Modifier
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
            allItemDataList = listOf(Item(url = "item/airplane.json", name = "책상")),
            addDialogChange = "area",
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