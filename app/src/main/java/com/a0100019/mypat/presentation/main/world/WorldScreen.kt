package com.a0100019.mypat.presentation.main.world

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.image.MapImage
import com.a0100019.mypat.presentation.image.DraggableImage
import com.a0100019.mypat.presentation.image.ItemImage
import com.a0100019.mypat.presentation.image.PatImage
import com.a0100019.mypat.presentation.main.world.dialog.DialogScreenContent
import com.a0100019.mypat.ui.theme.MypatTheme


@Composable
fun WorldScreen(
    mapUrl : String,
    patDataList : List<Pat>,
    patWorldDataList : List<World>,
    itemDataList : List<Item>,
    itemWorldDataList : List<World>,
    dialogPatId : String,
    dialogPatIdChange : (String) -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth() // 가로 크기는 최대
            .aspectRatio(1 / 1.25f), // 세로가 가로의 1.25배
        color = Color.Gray
    ) {

        // 다이얼로그 표시
        if (dialogPatId != "0") {
            Dialog(
                onDismissRequest = { dialogPatIdChange("0") }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f) // 화면의 60%만 차지
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        // 다이얼로그 안의 Screen
                        DialogScreenContent(
                            patData = patDataList.find { it.id.toString() == dialogPatId }!!,
                            onClose = { dialogPatIdChange("0") }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // Optional: Set background color
            contentAlignment = Alignment.Center // Center content
        ) {
            MapImage(mapUrl)

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
                    ItemImage(
                        itemUrl = itemData.url,
                        surfaceWidthDp = surfaceWidthDp,
                        surfaceHeightDp = surfaceHeightDp,
                        xFloat = itemData.x,
                        yFloat = itemData.y,
                        sizeFloat = itemData.sizeFloat
                    )
                }

                patDataList.map { patData ->
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



                DraggableImage(
                    itemUrl = "item/table.png",
                    initialX = 100.dp,
                    initialY = 100.dp,
                    size = 20.dp
                )

//                DraggableItemImage(
//                    itemUrl = "item/table.png",
//                    surfaceWidthDp = 300.dp, // 예제 너비
//                    surfaceHeightDp = 400.dp, // 예제 높이
//                    initialXFloat = firstItemData.x,
//                    initialYFloat = firstItemData.y,
//                    sizeFloat = 0.2f
//                ) { newX, newY ->
//                    firstItemData.x = newX
//                    firstItemData.y = newY
//                }

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
            dialogPatIdChange = { }
        )
    }
}