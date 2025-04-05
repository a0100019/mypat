package com.a0100019.mypat.presentation.information

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
import com.a0100019.mypat.presentation.ui.image.item.DraggableItemImage
import com.a0100019.mypat.presentation.ui.image.item.WorldItemImage
import com.a0100019.mypat.presentation.ui.image.pat.DraggablePatImage
import com.a0100019.mypat.presentation.ui.image.pat.PatImage
import com.a0100019.mypat.presentation.ui.image.pat.PatInformationImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import kotlinx.coroutines.flow.flowOf
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

        )
}



@Composable
fun InformationScreen(
    mapUrl : String,
    patDataList : List<Pat>,
    itemDataList : List<Item>,


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
                text = "#17"
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
        )
    }
}