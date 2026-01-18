package com.a0100019.mypat.presentation.activity.index

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.domain.AppBgmManager
import com.a0100019.mypat.presentation.ui.component.TextAutoResizeSingleLine
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

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("bgm_prefs", Context.MODE_PRIVATE)
    val bgmOn = prefs.getBoolean("bgmOn", true)

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
        if (bgmOn) {
            AppBgmManager.play()
        }
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
                .padding(start = 6.dp, end = 6.dp, bottom = 6.dp)
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

                JustImage(
                    filePath = "etc/exit.png",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(30.dp)
                        .clickable {
                            popBackStack()
                        }
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        pageList.chunked(3).forEach { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEachIndexed { i, pat ->

                                    val interactionSource = remember { MutableInteractionSource() }
                                    val isPressed by interactionSource.collectIsPressedAsState()
                                    val isLocked = pat.date == "0"
                                    val scale by animateFloatAsState(
                                        targetValue = if (isPressed) 0.96f else 1f,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                        label = "scale"
                                    )

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
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
                                            .aspectRatio(0.75f),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (!isLocked) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF2F2F2)
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 2.dp else 6.dp)
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                // 펫 이미지 컨테이너
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(18.dp))
                                                        .background(
                                                            if (isLocked) Color(0xFFE0E0E0)
                                                            else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            color = if (isLocked) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                                                            shape = RoundedCornerShape(18.dp)
                                                        )
                                                ) {
                                                    // 펫 이미지 (잠금 시 블러 제거, 투명도만 조절)
                                                    JustImage(
                                                        filePath = pat.url,
                                                        contentScale = ContentScale.Fit,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(8.dp)
                                                            .alpha(if (isLocked) 0.3f else 1f) // 블러가 없으므로 투명도를 살짝 올림(0.25 -> 0.3)
                                                    )

                                                }

                                                // 하단 펫 이름 영역
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 4.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    TextAutoResizeSingleLine(
                                                        text = pat.name, // 이름도 물음표로 할지 선택하세요!
                                                        modifier = Modifier.fillMaxWidth(),
                                                    )
                                                }
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        pageList.chunked(3).forEachIndexed { rowIdx, rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEachIndexed { colIdx, item ->
                                    val originalIndex = start + rowIdx * 3 + colIdx
                                    val isLocked = item.date == "0"

                                    val interactionSource = remember { MutableInteractionSource() }
                                    val isPressed by interactionSource.collectIsPressedAsState()
                                    val scale by animateFloatAsState(
                                        targetValue = if (isPressed) 0.96f else 1f,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                        label = "scale"
                                    )

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .graphicsLayer {
                                                scaleX = scale
                                                scaleY = scale
                                            }
                                            .clickable(
                                                interactionSource = interactionSource,
                                                indication = null,
                                                onClick = { onCardClick(originalIndex) }
                                            )
                                            .aspectRatio(0.75f),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (!isLocked)
                                                MaterialTheme.colorScheme.surfaceVariant
                                            else
                                                Color(0xFFF2F2F2)
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = if (isPressed) 2.dp else 6.dp
                                        )
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {

                                                // 🔹 이미지 컨테이너
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(18.dp))
                                                        .background(
                                                            if (isLocked) Color(0xFFE0E0E0)
                                                            else MaterialTheme.colorScheme.secondaryContainer
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            color = MaterialTheme.colorScheme.outlineVariant,
                                                            shape = RoundedCornerShape(18.dp)
                                                        )
                                                ) {

                                                    // 🔹 아이템 이미지 (잠금 시 투명도만 적용)
                                                    JustImage(
                                                        filePath = item.url,
                                                        contentScale = ContentScale.Fit,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(12.dp)
                                                            .alpha(if (isLocked) 0.3f else 1f)
                                                    )

                                                    // 🔹 하단 입체감 그라디언트 (유지)
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(
                                                                brush = Brush.verticalGradient(
                                                                    colors = listOf(
                                                                        Color.Transparent,
                                                                        Color.Black.copy(alpha = 0.05f)
                                                                    ),
                                                                    startY = 100f
                                                                )
                                                            )
                                                    )
                                                }

                                                // 🔹 하단 텍스트 영역
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    TextAutoResizeSingleLine(
                                                        text = item.name,
                                                        modifier = Modifier.fillMaxWidth(),
                                                    )
                                                }
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        pageList.chunked(3).forEach { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEach { area ->
                                    val originalIndex = allAreaDataList.indexOf(area)

                                    val interactionSource = remember { MutableInteractionSource() }
                                    val isPressed by interactionSource.collectIsPressedAsState()
                                    val scale by animateFloatAsState(
                                        targetValue = if (isPressed) 0.96f else 1f,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                        label = "scale"
                                    )

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(8.dp)
                                            .graphicsLayer {
                                                scaleX = scale
                                                scaleY = scale
                                            }
                                            .clickable(
                                                interactionSource = interactionSource,
                                                indication = null,
                                                onClick = { onCardClick(originalIndex) }
                                            )
                                            .aspectRatio(1f / 1.4f),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor =
                                            if (area.date != "0")
                                                MaterialTheme.colorScheme.surfaceVariant
                                            else
                                                Color(0xFFF2F2F2)
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = if (isPressed) 2.dp else 6.dp
                                        )
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {

                                                // 🔹 이미지 컨테이너
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(18.dp))
                                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                                        .border(
                                                            width = 1.dp,
                                                            color = MaterialTheme.colorScheme.outlineVariant,
                                                            shape = RoundedCornerShape(18.dp)
                                                        )
                                                ) {

                                                    // 🔹 메인 이미지 (잠금 시 투명도만)
                                                    JustImage(
                                                        filePath = area.url,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .alpha(if (area.date == "0") 0.4f else 1f)
                                                    )

                                                    // 🔹 하단 은은한 그라디언트
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(
                                                                brush = Brush.verticalGradient(
                                                                    colors = listOf(
                                                                        Color.Transparent,
                                                                        Color.Black.copy(alpha = 0.2f)
                                                                    ),
                                                                    startY = 300f
                                                                )
                                                            )
                                                    )

                                                }

                                                // 🔹 하단 텍스트 영역
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    TextAutoResizeSingleLine(
                                                        text = area.name,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
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

                    JustImage(
                        filePath = "etc/arrow.png",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onPageChangeClick(false) }
                            .graphicsLayer(rotationZ = 270f)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = page.toString() + "페이지"
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    JustImage(
                        filePath = "etc/arrow.png",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onPageChangeClick(true) }
                            .graphicsLayer(rotationZ = 90f)
                    )

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
            allAreaDataList = listOf(Area(url = "area/kingdom.webp", name = "aa"),Area(url = "area/kingdom.webp", name = "aa"),Area(url = "area/kingdom.webp", name = "aa"),Area(url = "area/kingdom.webp", name = "aa"),Area(url = "area/kingdom.webp", name = "aa"),Area(url = "area/kingdom.webp", name = "aa"),Area(url = "area/kingdom.webp", name = "aa"),Area(url = "area/kingdom.webp", name = "aa")),
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