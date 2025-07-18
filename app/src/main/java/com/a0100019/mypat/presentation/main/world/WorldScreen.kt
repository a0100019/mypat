package com.a0100019.mypat.presentation.main.world

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.main.mainDialog.ItemSettingDialog
import com.a0100019.mypat.presentation.ui.image.item.DraggableItemImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.pat.DraggablePatImage
import com.a0100019.mypat.presentation.main.mainDialog.PatSettingDialog
import com.a0100019.mypat.presentation.main.mainDialog.WorldAddDialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun WorldScreen(
    worldViewModel: WorldViewModel = hiltViewModel(),
    onMainNavigateClick: () -> Unit,
) {

    val worldState : WorldState = worldViewModel.collectAsState().value

    val context = LocalContext.current

    worldViewModel.collectSideEffect { sideEffect ->
        when(sideEffect) {
            is WorldSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            WorldSideEffect.NavigateToMainScreen -> onMainNavigateClick()
        }
    }

    WorldScreen(
        onWorldSelectClick = worldViewModel::onWorldSelectClick,
        onMainNavigateClick = onMainNavigateClick,

        patDataList = worldState.patDataList,
        itemDataList = worldState.itemDataList,
        worldDataList = worldState.worldDataList,
        userDataList = worldState.userDataList,

        dialogItemId = worldState.dialogItemId,
        dialogPatId = worldState.dialogPatId,
        mapUrl = worldState.areaData.value,

        dialogPatIdChange = worldViewModel::dialogPatIdChange,
        dialogItemIdChange = worldViewModel::dialogItemIdChange,
        onPatSizeUpClick = worldViewModel::onPatSizeUpClick,
        onItemSizeUpClick = worldViewModel::onItemSizeUpClick,
        onPatSizeDownClick = worldViewModel::onPatSizeDownClick,
        onItemSizeDownClick = worldViewModel::onItemSizeDownClick,
        onItemDrag = worldViewModel::onItemDrag,
        onPatDrag = worldViewModel::onPatDrag,
        worldDataDelete = worldViewModel::worldDataDelete,
        addDialogChange = worldState.addDialogChange,
        mapWorldData = worldState.areaData,
        allAreaDataList = worldState.allAreaDataList,
        showWorldAddDialog = worldState.showWorldAddDialog,
        onShowAddDialogClick = worldViewModel::onShowAddDialogClick,
        onAddPatClick = worldViewModel::onAddPatClick,
        onAddItemClick = worldViewModel::onAddItemClick,
        onSelectMapImageClick = worldViewModel::onSelectMapImageClick,
        onAddDialogChangeClick = worldViewModel::onAddDialogChangeClick,
        onPatEffectChangeClick = worldViewModel::onPatEffectChangeClick
    )


}

@Composable
fun WorldScreen(
    onWorldSelectClick: () -> Unit,
    onMainNavigateClick: () -> Unit,

    patDataList : List<Pat>,
    itemDataList : List<Item>,
    worldDataList : List<World>,
    userDataList: List<User>,
    allAreaDataList: List<Area>,

    dialogPatId : String,
    dialogItemId : String,
    mapUrl : String,
    showWorldAddDialog: Boolean,
    addDialogChange: String,
    mapWorldData: World,

    dialogPatIdChange : (String) -> Unit,
    dialogItemIdChange : (String) -> Unit,
    onPatSizeUpClick: () -> Unit,
    onItemSizeUpClick: () -> Unit,
    onPatSizeDownClick: () -> Unit,
    onItemSizeDownClick: () -> Unit,
    onItemDrag: (String, Float, Float) -> Unit,
    onPatDrag: (String, Float, Float) -> Unit,
    worldDataDelete: (String, String) -> Unit,
    onShowAddDialogClick: () -> Unit,
    onAddDialogChangeClick: () -> Unit,
    onSelectMapImageClick: (String) -> Unit,
    onAddPatClick: (String) -> Unit,
    onAddItemClick: (String) -> Unit,
    onPatEffectChangeClick: (Int) -> Unit = {}
) {

    // 다이얼로그 표시
    if (showWorldAddDialog) {
        WorldAddDialog(
            onClose = onShowAddDialogClick,
            allPatDataList = patDataList,
            allItemDataList = itemDataList,
            addDialogChange = addDialogChange,
            onAddDialogChangeClick = onAddDialogChangeClick,
            onSelectMapImageClick = onSelectMapImageClick,
            mapWorldData = mapWorldData,
            allAreaDataList = allAreaDataList,
            worldDataList = worldDataList,
            onAddItemClick = onAddItemClick,
            onAddPatClick = onAddPatClick,
            userDataList = userDataList,
        )
    }

    if (dialogPatId != "0") {
        PatSettingDialog(
            onDelete = {
                worldDataDelete(dialogPatId, "pat")
                dialogPatIdChange("0")
            },
            onDismiss = { dialogPatIdChange("0") },
            onSizeUp = onPatSizeUpClick,
            onSizeDown = onPatSizeDownClick,
            patData = patDataList.find { it.id.toString() == dialogPatId }!!,
            onPatEffectChangeClick = onPatEffectChangeClick
        )
    }

    if (dialogItemId != "0") {
        ItemSettingDialog(
            onDelete = {
                worldDataDelete(dialogItemId, "item")
                dialogItemIdChange("0")
            },
            onDismiss = { dialogItemIdChange("0") },
            onSizeUp = onItemSizeUpClick,
            onSizeDown = onItemSizeDownClick,
            itemData = itemDataList.find { it.id.toString() == dialogItemId }!!,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "꾸미기",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(top = 16.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "펫 ${userDataList.find { it.id == "pat" }?.value3} / ${userDataList.find { it.id == "pat" }?.value2}   " +
                            "아이템 ${userDataList.find { it.id == "item" }?.value3} / ${userDataList.find { it.id == "item" }?.value2}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth() // 가로 크기는 최대
                    .aspectRatio(1 / 1.25f)
                    .padding(start = 10.dp, end = 10.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.scrim,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer),
                shadowElevation = 6.dp,
            ) {

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

                        worldDataList.forEachIndexed { index, worldData ->
                            key("${worldData.id}_${worldData.type}") {
                                if (worldData.type == "pat") {
                                    patDataList.find { it.id.toString() == worldData.value }
                                        ?.let { patData ->

                                            DraggablePatImage(
                                                worldIndex = index.toString(),
                                                patUrl = patData.url,
                                                surfaceWidthDp = surfaceWidthDp,
                                                surfaceHeightDp = surfaceHeightDp,
                                                xFloat = patData.x,
                                                yFloat = patData.y,
                                                sizeFloat = patData.sizeFloat,
                                                effect = patData.effect,
                                                onClick = { dialogPatIdChange(patData.id.toString()) }
                                            ) { newXFloat, newYFloat ->
                                                onPatDrag(
                                                    patData.id.toString(),
                                                    newXFloat,
                                                    newYFloat
                                                )
                                            }

                                        }

                                } else {
                                    itemDataList.find { it.id.toString() == worldData.value }
                                        ?.let { itemData ->

                                            DraggableItemImage(
                                                worldIndex = index.toString(),
                                                itemUrl = itemData.url,
                                                surfaceWidthDp = surfaceWidthDp,
                                                surfaceHeightDp = surfaceHeightDp,
                                                xFloat = itemData.x,
                                                yFloat = itemData.y,
                                                sizeFloat = itemData.sizeFloat,
                                                onClick = { dialogItemIdChange(itemData.id.toString()) }
                                            ) { newXFloat, newYFloat ->
                                                onItemDrag(
                                                    itemData.id.toString(),
                                                    newXFloat,
                                                    newYFloat
                                                )
                                            }

                                        }
                                }
                            }
                        }

                    }

                }

            }
        }

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MainButton(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(bottom = 10.dp),
                    onClick = onShowAddDialogClick,
                    text = "수정 하기"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .weight(0.4f)
                ) {
                    MainButton(
                        modifier = Modifier
                            .fillMaxWidth(0.6f),
                        onClick = onMainNavigateClick,
                        text = "취소"
                    )
                }

                Spacer(modifier = Modifier.weight(0.2f))

                Row(
                    modifier = Modifier
                        .weight(0.4f)
                ) {
                    MainButton(
                        modifier = Modifier
                            .fillMaxWidth(0.6f),
                        onClick = onWorldSelectClick,
                        text = "확인"
                    )
                }
            }

        }

    }

}

@Preview(showBackground = true)
@Composable
fun WorldScreenPreview() {
    MypatTheme {
        WorldScreen(
            mapUrl = "area/beach.jpg",
            patDataList = listOf(Pat(url = "pat/cat.json")),
            itemDataList = listOf(Item(url = "item/table.png")),
            dialogPatId = "0",
            dialogItemId = "0",
            dialogPatIdChange = {},
            dialogItemIdChange = {},
            onPatSizeUpClick = {},
            onItemSizeUpClick = {},
            onPatSizeDownClick = {},
            onItemSizeDownClick = {},
            onItemDrag = { id, newX, newY -> },
            onPatDrag = { id, newX, newY -> },
            worldDataList = emptyList(),
            worldDataDelete = {_, _ ->},
            onWorldSelectClick = {},
            onMainNavigateClick = {},
            userDataList = emptyList(),
            onAddPatClick = {},
            onAddItemClick = {},
            onSelectMapImageClick = {},
            onShowAddDialogClick = {},
            onAddDialogChangeClick = {},
            mapWorldData = World(),
            showWorldAddDialog = false,
            addDialogChange = "",
            allAreaDataList = emptyList(),
        )
    }
}