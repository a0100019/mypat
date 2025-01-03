package com.a0100019.mypat.presentation.main.world

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.image.DisplayMapImage
import com.a0100019.mypat.presentation.image.DraggableImage
import com.a0100019.mypat.presentation.image.DraggableItemImage
import com.a0100019.mypat.presentation.image.ItemImage
import com.a0100019.mypat.presentation.image.PatImage
import com.a0100019.mypat.ui.theme.MypatTheme

//@Composable
//fun WorldScreen(
//    viewModel: WorldViewModel? = null
//
//) {
//
//    val state : WorldState = viewModel?.collectAsState()!!.value
//
//    val context = LocalContext.current
//
//    viewModel.collectSideEffect { sideEffect ->
//        when (sideEffect) {
//            is WorldSideEffect.Toast ->
//                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
//
//        }
//    }
//
//
//    WorldScreen(
//        value = "스크린 나누기"
//    )
//}



@Composable
fun WorldScreen(
    mapUrl : String,
    firstPatData : Pat,
    firstPatWorldData : World,
    firstItemData : Item,
    firstItemWorldData : World
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth() // 가로 크기는 최대
            .aspectRatio(1 / 1.25f) // 세로가 가로의 1.25배
            .padding(10.dp), // padding 추가
        color = Color.Gray
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // Optional: Set background color
            contentAlignment = Alignment.Center // Center content
        ) {
            DisplayMapImage(mapUrl)

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val density = LocalDensity.current

                // Surface 크기 가져오기 (px → dp 변환)
                val surfaceWidth = constraints.maxWidth
                val surfaceHeight = constraints.maxHeight

                val surfaceWidthDp = with(density) { surfaceWidth.toDp() }
                val surfaceHeightDp = with(density) { surfaceHeight.toDp() }

                ItemImage(
                    itemUrl = firstItemData.url,
                    surfaceWidthDp = surfaceWidthDp,
                    surfaceHeightDp = surfaceHeightDp,
                    xFloat = firstItemData.x,
                    yFloat = firstItemData.y,
                    sizeFloat = firstItemData.sizeFloat
                )

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

                PatImage(
                    patUrl = firstPatData.url,
                    surfaceWidthDp = surfaceWidthDp,
                    surfaceHeightDp = surfaceHeightDp,
                    xFloat = firstPatData.x,
                    yFloat = firstPatData.y,
                    sizeFloat = firstPatData.sizeFloat
                )



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
            firstPatData = Pat(url = "pat/cat.json"),
            firstPatWorldData = World(id = "pat1"),
            firstItemData = Item(url = "item/table.png"),
            firstItemWorldData = World(id = "item1")
        )
    }
}