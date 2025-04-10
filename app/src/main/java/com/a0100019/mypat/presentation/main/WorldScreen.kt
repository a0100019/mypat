package com.a0100019.mypat.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.main.mainDialog.ItemSettingDialog
import com.a0100019.mypat.presentation.ui.image.item.DraggableItemImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.item.WorldItemImage
import com.a0100019.mypat.presentation.ui.image.pat.DraggablePatImage
import com.a0100019.mypat.presentation.main.mainDialog.PatDialog
import com.a0100019.mypat.presentation.main.mainDialog.PatSettingDialog
import com.a0100019.mypat.presentation.ui.image.pat.PatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


@Composable
fun WorldScreen(
    mapUrl : String,
    patDataList : List<Pat>,
    patWorldDataList : List<World>,
    patFlowWorldDataList : Flow<List<Pat>>,
    itemDataList : List<Item>,
    itemWorldDataList : List<World>,
    dialogPatId : String,
    dialogItemId : String,
    dialogPatIdChange : (String) -> Unit,
    dialogItemIdChange : (String) -> Unit,
    onFirstGameNavigateClick: () -> Unit,
    onSecondGameNavigateClick: () -> Unit,
    onThirdGameNavigateClick: () -> Unit,
    worldChange: Boolean,
    patWorldDataDelete: (String) -> Unit,
    itemWorldDataDelete: (String) -> Unit,
    onPatSizeUpClick: () -> Unit,
    onItemSizeUpClick: () -> Unit,
    onPatSizeDownClick: () -> Unit,
    onItemSizeDownClick: () -> Unit,
    onItemDrag: (String, Float, Float) -> Unit,
    onPatDrag: (String, Float, Float) -> Unit,
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth() // 가로 크기는 최대
            .aspectRatio(1 / 1.25f), // 세로가 가로의 1.25배
        color = Color.Gray
    ) {

        //flow데이터 쓰는법
        val patFlow by patFlowWorldDataList.collectAsState(initial = emptyList())
        // 다이얼로그 표시
        if (!worldChange && dialogPatId != "0") {
            PatDialog(
                onClose = { dialogPatIdChange("0") },
                patData = patDataList.find { it.id.toString() == dialogPatId }!!,
                patFlowData = patFlow.find { it.id.toString() == dialogPatId},
                onFirstGameNavigateClick = onFirstGameNavigateClick,
                onSecondGameNavigateClick = onSecondGameNavigateClick,
                onThirdGameNavigateClick = onThirdGameNavigateClick
            )
        }

        if (worldChange && dialogPatId != "0") {
            PatSettingDialog(
                onDelete = {
                    patWorldDataDelete(dialogPatId)
                    dialogPatIdChange("0")
                },
                onDismiss = { dialogPatIdChange("0") },
                onSizeUp = onPatSizeUpClick,
                onSizeDown = onPatSizeDownClick,
                patData = patDataList.find { it.id.toString() == dialogPatId }!!,
            )
        }

        if (worldChange && dialogItemId != "0") {
            ItemSettingDialog(
                onDelete = {
                    itemWorldDataDelete(dialogItemId)
                    dialogItemIdChange("0")
                },
                onDismiss = { dialogItemIdChange("0") },
                onSizeUp = onItemSizeUpClick,
                onSizeDown = onItemSizeDownClick,
                itemData = itemDataList.find { it.id.toString() == dialogItemId }!!,
            )
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // Optional: Set background color
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
                    if(worldChange) {
                        DraggableItemImage(
                            itemUrl = itemData.url,
                            surfaceWidthDp = surfaceWidthDp,
                            surfaceHeightDp = surfaceHeightDp,
                            xFloat = itemData.x,
                            yFloat = itemData.y,
                            sizeFloat = itemData.sizeFloat,
                            onClick = { dialogItemIdChange(itemData.id.toString()) }
                        ) { newXFloat, newYFloat ->
                            onItemDrag(itemData.id.toString(), newXFloat, newYFloat)
                        }
                    } else {
                        WorldItemImage(
                            itemUrl = itemData.url,
                            surfaceWidthDp = surfaceWidthDp,
                            surfaceHeightDp = surfaceHeightDp,
                            xFloat = itemData.x,
                            yFloat = itemData.y,
                            sizeFloat = itemData.sizeFloat
                        )
                    }
                }

                patDataList.map { patData ->
                    if(worldChange) {
                        DraggablePatImage(
                            patUrl = patData.url,
                            surfaceWidthDp = surfaceWidthDp,
                            surfaceHeightDp = surfaceHeightDp,
                            xFloat = patData.x,
                            yFloat = patData.y,
                            sizeFloat = patData.sizeFloat,
                            onClick = { dialogPatIdChange(patData.id.toString()) }
                        ) { newXFloat, newYFloat ->
                            onPatDrag(patData.id.toString(), newXFloat, newYFloat)
                        }
                    } else {
                        PatImage(
                            patUrl = patData.url,
                            surfaceWidthDp = surfaceWidthDp,
                            surfaceHeightDp = surfaceHeightDp,
                            xFloat = patData.x,
                            yFloat = patData.y,
                            sizeFloat = patData.sizeFloat,
                            onClick = { dialogPatIdChange(patData.id.toString()) }
                        )
                    }
                }




            }

        }

    }

    // Fullscreen container

}

@Preview(showBackground = true)
@Composable
fun SelectScreenPreview() {
    MypatTheme {
        WorldScreen(
            mapUrl = "map/beach.jpg",
            patDataList = listOf(Pat(url = "pat/cat.json")),
            patWorldDataList = listOf(World(id = "pat1")),
            itemDataList = listOf(Item(url = "item/table.png")),
            itemWorldDataList = listOf(World(id = "item1")),
            dialogPatId = "0",
            dialogItemId = "0",
            dialogPatIdChange = { },
            dialogItemIdChange = {},
            onFirstGameNavigateClick = { },
            onSecondGameNavigateClick = { },
            onThirdGameNavigateClick = { },
            worldChange = false,
            patWorldDataDelete = { },
            itemWorldDataDelete = {},
            onPatSizeUpClick = {  },
            onItemSizeUpClick = {},
            onPatSizeDownClick = {  },
            onItemSizeDownClick = {},
            onItemDrag = { id, newX, newY -> },
            onPatDrag = { id, newX, newY -> },
            patFlowWorldDataList = flowOf(emptyList()),
        )
    }
}