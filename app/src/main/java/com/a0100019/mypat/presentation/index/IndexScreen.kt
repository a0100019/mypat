package com.a0100019.mypat.presentation.index

import android.widget.Toast
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
            open = allItemDataList.getOrNull(dialogItemIndex)!!.date != "0",
            itemData = allItemDataList.getOrNull(dialogItemIndex)!!
        )
    } else if(dialogAreaIndex != -1 && typeChange == "area") {
        IndexAreaDialog(
            onClose = onCloseDialog,
            open = allAreaDataList.getOrNull(dialogAreaIndex)!!.date != "0",
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
            "pat" -> LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val reversedList = allPatDataList.asReversed() // 역순 리스트 생성

                items(reversedList.size) { index ->
                    val pat = reversedList[index] // 현재 아이템

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .aspectRatio(0.7f)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple(bounded = true, color = Color.White),
                                onClick = {
                                    val originalIndex = allPatDataList.size - 1 - index // 원래 리스트 기준 인덱스
                                    onCardClick(originalIndex)
                                }
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.scrim
                        ),
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .background(
                                            color = if (pat.date != "0") {
                                                MaterialTheme.colorScheme.tertiaryContainer
                                            } else {
                                                Color.LightGray
                                            },
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    DialogPatImage(pat.url)
                                    if (pat.date == "0") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Color.LightGray.copy(alpha = 0.5f),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                        )
                                    }
                                }

                                Text(
                                    text = pat.name,
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }

                            if (pat.date == "0") {
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
            "item" -> LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val reversedList = allItemDataList.asReversed() // 역순 리스트 생성

                items(reversedList.size) { index ->
                    val item = reversedList[index] // 현재 아이템

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .aspectRatio(0.7f)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple(bounded = true, color = Color.White),
                                onClick = {
                                    val originalIndex = allItemDataList.size - 1 - index
                                    onCardClick(originalIndex)
                                }
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.scrim
                        ),
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .background(
                                            color = if (item.date != "0") {
                                                MaterialTheme.colorScheme.tertiaryContainer
                                            } else {
                                                Color.LightGray
                                            },
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    JustImage(
                                        filePath = item.url,
                                        contentScale = ContentScale.Fit
                                    )
                                    if(item.date == "0") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Color.LightGray.copy(alpha = 0.5f),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                        )
                                    }
                                }

                                Text(
                                    text = item.name,
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }

                            if (item.date == "0") {
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

            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val reversedList = allAreaDataList.asReversed() // 역순 리스트

                items(reversedList.size) { index ->
                    val area = reversedList[index]

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .aspectRatio(0.8f)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple(bounded = true, color = Color.White),
                                onClick = {
                                    val originalIndex = allAreaDataList.size - 1 - index
                                    onCardClick(originalIndex)
                                }
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.scrim
                        ),
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = if (area.date != "0") {
                                                MaterialTheme.colorScheme.tertiaryContainer
                                            } else {
                                                Color.LightGray
                                            },
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    JustImage(
                                        filePath = area.url,
                                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                                    )
                                    if(area.date == "0") {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f)
                                                .background(
                                                    Color.LightGray.copy(alpha = 0.8f),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                        )
                                    }
                                }

                                Text(
                                    text = area.name,
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }

                            if (area.date == "0") {
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
            allAreaDataList = listOf(Area(url = "area/forest.jpg")),
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