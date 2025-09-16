package com.a0100019.mypat.presentation.index

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.domain.AppBgmManager
import com.a0100019.mypat.presentation.ui.component.AutoResizeSingleLineText
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun IndexScreen(
    indexViewModel: IndexViewModel = hiltViewModel(),
    popBackStack: () -> Unit = {}

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
        popBackStack = popBackStack,
        onPageChangeClick = indexViewModel::onPageChangeClick,

        typeChange = indexState.typeChange,
        dialogPatIndex = indexState.dialogPatIndex,
        dialogItemIndex = indexState.dialogItemIndex,
        dialogAreaIndex = indexState.dialogAreaIndex,
        page = indexState.page
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
    popBackStack: () -> Unit = {},
    onPageChangeClick: (Boolean) -> Unit = {},

    typeChange: String,
    dialogPatIndex: Int,
    dialogItemIndex: Int,
    dialogAreaIndex: Int,
    page: Int = 1
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
        AppBgmManager.pause()
        IndexAreaDialog(
            onClose = onCloseDialog,
            open = allAreaDataList.getOrNull(dialogAreaIndex)!!.date != "0",
            areaData = allAreaDataList.getOrNull(dialogAreaIndex)!!
        )
    } else {
        AppBgmManager.play()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        // Fullscreen container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // 가운데 텍스트
                Text(
                    text = "도감",
                    style = MaterialTheme.typography.displaySmall
                )

                // 오른쪽 버튼
                MainButton(
                    text = "닫기",
                    onClick = popBackStack,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

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
                            text = "${allPatDataList.count { it.date != "0" }}/${allPatDataList.size}",
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
                            text = "${allItemDataList.count { it.date != "0" }}/${allItemDataList.size}",
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
                            text = "${allAreaDataList.count { it.date != "0" }}/${allAreaDataList.size}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(12.dp)
                        )
                    }
                }

            }

            when (typeChange) {
                "pat" -> {
                    val perPage = 9
                    val safePage = page.coerceAtLeast(1)
                    val start = (safePage - 1) * perPage
                    val end = minOf(start + perPage, allPatDataList.size)

                    // 👉 10개일 때 page=2면 start=9, end=10 → 1개만 노출
                    val pageList =
                        if (start < end) allPatDataList.subList(start, end) else emptyList()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        pageList.chunked(3).forEach { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEachIndexed { i, pat ->

                                    val interactionSource = remember { MutableInteractionSource() }
                                    val isPressed by interactionSource.collectIsPressedAsState()
                                    val scale by animateFloatAsState(
                                        if (isPressed) 0.95f else 1f,
                                        label = "scale"
                                    )

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                                            .graphicsLayer { scaleX = scale; scaleY = scale }
                                            .clickable(
                                                interactionSource = interactionSource,
                                                indication = null,
                                                onClick = {
                                                    onCardClick(
                                                        start + rowItems.indexOf(pat) + (pageList.indexOf(
                                                            rowItems.first()
                                                        ) / 3) * 3
                                                    )
                                                }
                                            )
                                            .border(
                                                2.dp,
                                                MaterialTheme.colorScheme.primaryContainer,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .aspectRatio(0.75f),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.scrim),
                                    ) {
                                        Box(Modifier.fillMaxSize()) {
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
                                                            color = if (pat.date != "0") MaterialTheme.colorScheme.tertiaryContainer else Color.LightGray,
                                                            shape = RoundedCornerShape(16.dp)
                                                        )
                                                        .border(
                                                            2.dp,
                                                            MaterialTheme.colorScheme.onTertiaryContainer,
                                                            RoundedCornerShape(16.dp)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    JustImage(filePath = pat.url)
                                                    if (pat.date == "0") {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .background(
                                                                    Color.LightGray.copy(
                                                                        alpha = 0.5f
                                                                    ),
                                                                    shape = RoundedCornerShape(16.dp)
                                                                )
                                                        )
                                                    }
                                                }

                                                AutoResizeSingleLineText(
                                                    text = pat.name,
                                                    modifier = Modifier.padding(top = 10.dp)
                                                        .fillMaxWidth()
                                                )
                                            }

                                            if (pat.date == "0") {
                                                JustImage(
                                                    filePath = "etc/lock.png",
                                                    modifier = Modifier.size(35.dp)
                                                        .align(Alignment.TopStart).padding(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // 마지막 줄에서 3칸 미만이면 빈 칸 채우기
                                if (rowItems.size < 3) {
                                    repeat(3 - rowItems.size) {
                                        Spacer(modifier = Modifier.weight(1f).padding(6.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                "item" -> {
                    val perPage = 9
                    val safePage = page.coerceAtLeast(1)
                    val start = (safePage - 1) * perPage
                    val end = minOf(start + perPage, allItemDataList.size)

                    val pageList =
                        if (start < end) allItemDataList.subList(start, end) else emptyList()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        pageList.chunked(3).forEachIndexed { rowIdx, rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEachIndexed { colIdx, item ->
                                    val originalIndex = start + rowIdx * 3 + colIdx

                                    val interactionSource = remember { MutableInteractionSource() }
                                    val isPressed by interactionSource.collectIsPressedAsState()
                                    val scale by animateFloatAsState(
                                        targetValue = if (isPressed) 0.95f else 1f,
                                        label = "scale"
                                    )

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                                            .graphicsLayer { scaleX = scale; scaleY = scale }
                                            .clickable(
                                                interactionSource = interactionSource,
                                                indication = null,
                                                onClick = { onCardClick(originalIndex) } // ✅ 역순 계산 없음
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .aspectRatio(0.75f),
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
                                                            color = if (item.date != "0")
                                                                MaterialTheme.colorScheme.tertiaryContainer
                                                            else
                                                                Color.LightGray,
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
                                                    JustImage(filePath = item.url)

                                                    if (item.date == "0") {
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

                                                AutoResizeSingleLineText(
                                                    text = item.name,
                                                    modifier = Modifier
                                                        .padding(top = 10.dp)
                                                        .fillMaxWidth()
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

                                // 마지막 줄이 3칸 미만이면 빈 칸 채우기
                                if (rowItems.size < 3) {
                                    repeat(3 - rowItems.size) {
                                        Spacer(modifier = Modifier.weight(1f).padding(6.dp))
                                    }
                                }
                            }
                        }

                    }
                }

                else -> {
                    val perPage = 9
                    val safePage = page.coerceAtLeast(1)
                    val start = (safePage - 1) * perPage
                    val end = minOf(start + perPage, allAreaDataList.size)

                    // 예: 10개면 page=2 → start=9, end=10 → 1개만 노출
                    val pageList =
                        if (start < end) allAreaDataList.subList(start, end) else emptyList()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        pageList.chunked(3).forEach { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEach { area ->
                                    val originalIndex = allAreaDataList.indexOf(area)

                                    val interactionSource = remember { MutableInteractionSource() }
                                    val isPressed by interactionSource.collectIsPressedAsState()
                                    val scale by animateFloatAsState(
                                        targetValue = if (isPressed) 0.95f else 1f,
                                        label = "scale"
                                    )

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                                            .graphicsLayer { scaleX = scale; scaleY = scale }
                                            .clickable(
                                                interactionSource = interactionSource,
                                                indication = null,
                                                onClick = { onCardClick(originalIndex) } // ✅ 역순 계산 없음
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .aspectRatio(1f/1.35f)
                                        ,
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.scrim
                                        ),
                                    ) {
                                        Box(
                                            Modifier
//                                                .fillMaxSize()
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.SpaceBetween,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .aspectRatio(1f/1.2f)
                                                        .fillMaxWidth()
                                                        .background(
                                                            color = if (area.date != "0")
                                                                MaterialTheme.colorScheme.tertiaryContainer
                                                            else
                                                                Color.LightGray,
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
                                                        contentScale = ContentScale.FillBounds,
                                                        modifier = Modifier.clip(
                                                            RoundedCornerShape(
                                                                16.dp
                                                            )
                                                        )
                                                    )
                                                    if (area.date == "0") {
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

                                                AutoResizeSingleLineText(
                                                    text = area.name,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
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

                                // 마지막 줄이 3칸 미만이면 빈 칸 채우기
                                if (rowItems.size < 3) {
                                    repeat(3 - rowItems.size) {
                                        Spacer(modifier = Modifier.weight(1f).padding(6.dp))
                                    }
                                }
                            }
                        }

                    }
                }
            }

            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ){

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        modifier = Modifier
                            .padding(top = 6.dp),
                        color = Color.Transparent, // ✅ 배경 투명
                    ) {
                        MainButton(
                            onClick = { onPageChangeClick(false) },
                            text = "   이전   ",
                            modifier = Modifier
                            ,
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = page.toString() + "페이지"
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Surface(
                        modifier = Modifier
                            .padding(top = 6.dp),
                        color = Color.Transparent, // ✅ 배경 투명
                    ) {
                        MainButton(
                            onClick = { onPageChangeClick(true) },
                            text = "   다음   ",
                            modifier = Modifier
                            ,
                        )
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
                                .padding(top = 6.dp),
                            color = Color.Transparent, // ✅ 배경 투명
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
    }
}

@Preview(showBackground = true)
@Composable
fun IndexScreenPreview() {
    MypatTheme {
        IndexScreen(
            allPatDataList = listOf(Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json")),
            allItemDataList = listOf(Item(url = "item/airplane.json")),
            allAreaDataList = listOf(Area(url = "area/kingdom.png", name = "aa"),Area(url = "area/kingdom.png", name = "aa"),Area(url = "area/kingdom.png", name = "aa"),Area(url = "area/kingdom.png", name = "aa"),Area(url = "area/kingdom.png", name = "aa"),Area(url = "area/kingdom.png", name = "aa"),Area(url = "area/kingdom.png", name = "aa"),Area(url = "area/kingdom.png", name = "aa")),
            onTypeChangeClick = {},
            typeChange = "area",
            dialogPatIndex = -1,
            onCloseDialog = {},
            onCardClick = {},
            dialogItemIndex = -1,
            dialogAreaIndex = -1,
        )
    }
}