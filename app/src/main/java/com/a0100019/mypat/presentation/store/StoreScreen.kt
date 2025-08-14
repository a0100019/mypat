package com.a0100019.mypat.presentation.store

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.index.IndexItemDialog
import com.a0100019.mypat.presentation.index.IndexAreaDialog
import com.a0100019.mypat.presentation.index.IndexPatDialog
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun StoreScreen(
    storeViewModel: StoreViewModel = hiltViewModel()

) {

    val storeState : StoreState = storeViewModel.collectAsState().value

    val context = LocalContext.current

    storeViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is StoreSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()

        }
    }

    StoreScreen(
        onDialogCloseClick = storeViewModel::onDialogCloseClick,
        onPatRoomUpClick = storeViewModel::onPatRoomUpClick,
        onSimpleDialog = storeViewModel::onSimpleDialog,
        onItemRoomUpClick = storeViewModel::onItemRoomUpClick,
        onNameTextChange = storeViewModel::onNameTextChange,
        changeShowDialog = storeViewModel::changeShowDialog,
        onNameChangeConfirm = storeViewModel::onNameChangeConfirm,
        onNameChangeClick = storeViewModel::onNameChangeClick,
        onMoneyChangeClick = storeViewModel::onMoneyChangeClick,
        onPatStoreClick = storeViewModel::onPatStoreClick,
        onPatEggClick = storeViewModel::onPatEggClick,
        onPatSelectClick = storeViewModel::onPatSelectClick,
        onItemClick = storeViewModel::onItemClick,
        onItemStoreClick = storeViewModel::onItemStoreClick,
        onItemSelectClick = storeViewModel::onItemSelectClick,
        onItemSelectCloseClick = storeViewModel::onItemSelectCloseClick,

        newPat = storeState.newPat,
        userData = storeState.userData,
        newItem = storeState.newItem,
        newArea = storeState.newArea,
        showDialog = storeState.showDialog,
        simpleDialogState = storeState.simpleDialogState,
        newName = storeState.newName,
        patEggDataList = storeState.patEggDataList,
        patStoreDataList = storeState.patStoreDataList,
        patSelectIndexList = storeState.patSelectIndexList,
        selectPatData = storeState.selectPatData,
        selectItemData = storeState.selectItemData,
        selectAreaData = storeState.selectAreaData,
        shuffledItemDataList = storeState.shuffledItemDataList,
        patPrice = storeState.patPrice,
        itemPrice = storeState.itemPrice,
        patSpacePrice = storeState.patSpacePrice,
        itemSpacePrice = storeState.itemSpacePrice

    )
}

@Composable
fun StoreScreen(
    onDialogCloseClick: () -> Unit,
    onPatRoomUpClick: () -> Unit,
    onItemRoomUpClick: () -> Unit,
    onSimpleDialog: (String) -> Unit,
    onNameTextChange: (String) -> Unit,
    changeShowDialog: (String) -> Unit,
    onNameChangeClick: () -> Unit,
    onNameChangeConfirm: () -> Unit,
    onMoneyChangeClick: () -> Unit,
    onPatStoreClick: () -> Unit,
    onPatEggClick: (Int) -> Unit,
    onPatSelectClick: () -> Unit,
    onItemClick: (String) -> Unit,
    onItemStoreClick: () -> Unit,
    onItemSelectClick: () -> Unit,
    onItemSelectCloseClick: () -> Unit,

    newPat: Pat?,
    newItem: Item?,
    newArea: Area?,
    userData: List<User>,
    showDialog: String,
    simpleDialogState: String,
    newName: String,
    patEggDataList: List<Pat>?,
    patStoreDataList: List<Pat>?,
    patSelectIndexList: List<Int>,
    selectPatData: Pat?,
    selectItemData: Item?,
    selectAreaData: Area?,
    shuffledItemDataList: List<String>?,
    patPrice: Int = 0,
    itemPrice: Int = 0,
    patSpacePrice: Int = 0,
    itemSpacePrice: Int = 0,

) {

    if (selectPatData != null) {
        PatSelectDialog(
            onSelectClick = onPatSelectClick,
            patData = selectPatData
        )
    }

    if (selectItemData != null) {
        ItemSelectDialog(
            onCloseClick = onItemSelectCloseClick,
            onSelectClick = onItemSelectClick,
            itemData = "${selectItemData.url}@${selectItemData.name}"
        )
    }

    if (selectAreaData != null) {
        ItemSelectDialog(
            onCloseClick = onItemSelectCloseClick,
            onSelectClick = onItemSelectClick,
            itemData = "${selectAreaData.url}@${selectAreaData.name}"
        )
    }

    // 다이얼로그 표시
    if (newPat != null) {
        IndexPatDialog(
            onClose = onDialogCloseClick,
            patData = newPat,
        )
    }

    if (newItem != null) {
        IndexItemDialog(
            onClose = onDialogCloseClick,
            itemData = newItem,
        )
    }

    if (newArea != null) {
        IndexAreaDialog(
            onClose = onDialogCloseClick,
            areaData = newArea,
        )
    }

    when (showDialog) {
        "pat" -> RoomUpDialog(
            onClose = onDialogCloseClick,
            userData = userData,
            showRoomUpDialog = showDialog
        )
        "item" -> RoomUpDialog(
            onClose = onDialogCloseClick,
            userData = userData,
            showRoomUpDialog = showDialog
        )
        "name" -> NameChangeDialog(
            onClose = onDialogCloseClick,
            userData = userData,
            onNameTextChange = onNameTextChange,
            onConfirmClick = onNameChangeConfirm,
            newName = newName
        )
        "patStore" -> PatStoreDialog(
            onClose = { },
            patData = patStoreDataList,
            patEggData = patEggDataList,
            onPatEggClick = onPatEggClick,
            selectIndexList = patSelectIndexList
        )
        "itemStore" -> ItemStoreDialog(
            onClose = { },
            itemData = shuffledItemDataList,
            onItemClick = onItemClick,
        )

    }

    if (simpleDialogState != "") {
        SimpleAlertDialog (
            onDismiss = { onSimpleDialog("") },
            onConfirm = {
                when(simpleDialogState) {
                    "펫을 뽑으시겠습니까?" -> onPatStoreClick()
                    "아이템을 뽑으시겠습니까?" -> onItemStoreClick()
                    "펫 공간을 늘리겠습니까?" -> onPatRoomUpClick()
                    "아이템 공간을 늘리겠습니까?" -> onItemRoomUpClick()
                    "가능한 닉네임입니다 변경하겠습니까?" -> onNameChangeClick()
                    "화폐를 변경하겠습니까?" -> onMoneyChangeClick()
                }
                onSimpleDialog("")
            },
            text = simpleDialogState
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "상점",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .padding(top = 10.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
            ,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "money : ${userData.find { it.id == "money" }?.value}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "cash : ${userData.find {it.id == "money"}?.value2}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                Text(
                    text = "아래로 드래그하세요",
                    style = MaterialTheme.typography.labelMedium
                )

            }

            item {
                //버튼 기본 설정
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    label = "scale"
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.scrim,
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(bounded = true, color = Color.White),
                            onClick = { onSimpleDialog("펫을 뽑으시겠습니까?") }
                        )
                        .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 6.dp)
                ) {
                    Box {

//                        Row(
//                            modifier = Modifier
//                                .align(Alignment.CenterStart)
//                        ) {
//                            Spacer(modifier = Modifier.size(10.dp))
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(50.dp)
//                                    .rotate(10f)
//                            )
//                        }
//
//                        Row(
//                            modifier = Modifier
//                                .align(Alignment.CenterEnd)
//                        ) {
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .rotate(-10f)
//                                    .align(Alignment.Bottom)
//                            )
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(30.dp)
//                                    .rotate(10f)
//                                    .align(Alignment.Top)
//                            )
//                            Spacer(modifier = Modifier.size(width = 10.dp, height = 50.dp))
//                        }

                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "펫 뽑기",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "랜덤으로 하나의 펫을 획득할 수 있습니다",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "$patPrice 햇살",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier,
                            )
                        }
                    }

                }
            }

            item {
                //버튼 기본 설정
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    label = "scale"
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.scrim,
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(bounded = true, color = Color.White),
                            onClick = { onSimpleDialog("아이템을 뽑으시겠습니까?") }
                        )
                        .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 6.dp)
                ) {
                    Box {

//                        Row(
//                            modifier = Modifier
//                                .align(Alignment.CenterStart)
//                        ) {
//                            Spacer(modifier = Modifier.size(10.dp))
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(50.dp)
//                                    .rotate(10f)
//                            )
//                        }
//
//                        Row(
//                            modifier = Modifier
//                                .align(Alignment.CenterEnd)
//                        ) {
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .rotate(-10f)
//                                    .align(Alignment.Bottom)
//                            )
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(30.dp)
//                                    .rotate(10f)
//                                    .align(Alignment.Top)
//                            )
//                            Spacer(modifier = Modifier.size(width = 10.dp, height = 50.dp))
//                        }

                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "아이템 뽑기",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "랜덤한 5개의 아이템 중 하나를 선택할 수 있습니다",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "(낮은 확률로 맵이 등장합니다)",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "$itemPrice 달빛",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier,
                            )
                        }
                    }

                }
            }

            item {
                //버튼 기본 설정
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    label = "scale"
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.scrim,
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(bounded = true, color = Color.White),
                            onClick = { onSimpleDialog("펫 공간을 늘리겠습니까?") }
                        )
                        .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 6.dp)
                ) {
                    Box {

//                        Row(
//                            modifier = Modifier
//                                .align(Alignment.CenterStart)
//                        ) {
//                            Spacer(modifier = Modifier.size(10.dp))
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(50.dp)
//                                    .rotate(10f)
//                            )
//                        }
//
//                        Row(
//                            modifier = Modifier
//                                .align(Alignment.CenterEnd)
//                        ) {
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .rotate(-10f)
//                                    .align(Alignment.Bottom)
//                            )
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(30.dp)
//                                    .rotate(10f)
//                                    .align(Alignment.Top)
//                            )
//                            Spacer(modifier = Modifier.size(width = 10.dp, height = 50.dp))
//                        }

                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "펫 공간 늘리기",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "마을의 펫 공간이 한 칸 늘어납니다",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "(현재 ${userData.find{it.id == "pat"}?.value2}칸, 최대 ${userData.find{it.id == "pat"}?.value}칸)",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = if(userData.find{it.id == "pat"}?.value2 != userData.find{it.id == "pat"}?.value) "$patSpacePrice 달빛" else "- 달빛",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier,
                            )
                        }
                    }

                }
            }

            item {
                //버튼 기본 설정
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    label = "scale"
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.scrim,
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(bounded = true, color = Color.White),
                            onClick = { onSimpleDialog("아이템 공간을 늘리겠습니까?") }
                        )
                        .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 6.dp)
                ) {
                    Box {

//                        Row(
//                            modifier = Modifier
//                                .align(Alignment.CenterStart)
//                        ) {
//                            Spacer(modifier = Modifier.size(10.dp))
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(50.dp)
//                                    .rotate(10f)
//                            )
//                        }
//
//                        Row(
//                            modifier = Modifier
//                                .align(Alignment.CenterEnd)
//                        ) {
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .rotate(-10f)
//                                    .align(Alignment.Bottom)
//                            )
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(30.dp)
//                                    .rotate(10f)
//                                    .align(Alignment.Top)
//                            )
//                            Spacer(modifier = Modifier.size(width = 10.dp, height = 50.dp))
//                        }

                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "아이템 공간 늘리기",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "마을의 아이템 공간이 한 칸 늘어납니다",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "(현재 ${userData.find{it.id == "item"}?.value2}칸, 최대 ${userData.find{it.id == "item"}?.value}칸)",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = if(userData.find{it.id == "item"}?.value2 != userData.find{it.id == "item"}?.value) "$itemSpacePrice 달빛" else "- 달빛",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier,
                            )
                        }
                    }

                }
            }

            item {
                //버튼 기본 설정
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    label = "scale"
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.scrim,
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(bounded = true, color = Color.White),
                            onClick = { changeShowDialog("name") }
                        )
                        .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 6.dp)
                ) {
                    Box {

//                        Row(
//                            modifier = Modifier
//                                .align(Alignment.CenterStart)
//                        ) {
//                            Spacer(modifier = Modifier.size(10.dp))
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(50.dp)
//                                    .rotate(10f)
//                            )
//                        }
//
//                        Row(
//                            modifier = Modifier
//                                .align(Alignment.CenterEnd)
//                        ) {
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .rotate(-10f)
//                                    .align(Alignment.Bottom)
//                            )
//                            JustImage(
//                                filePath = "pat/cat.json",
//                                modifier = Modifier
//                                    .size(30.dp)
//                                    .rotate(10f)
//                                    .align(Alignment.Top)
//                            )
//                            Spacer(modifier = Modifier.size(width = 10.dp, height = 50.dp))
//                        }

                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "닉네임 변경",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "현재 닉네임 : ${userData.find{it.id == "name"}?.value}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text =
                                    if(userData.find{it.id == "name"}?.value == "유저") "0 햇살" else "5 햇살"
                                ,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier,
                            )
                        }
                    }

                }
            }

            item {
                //버튼 기본 설정
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    label = "scale"
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.scrim,
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(bounded = true, color = Color.White),
                            onClick = { onSimpleDialog("화폐를 변경하겠습니까?") }
                        )
                        .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 6.dp)
                ) {
                    Box {

                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "화폐 교환",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
                            )
                            Text(
                                text = "햇살 1개 -> 달빛 3000개",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                ,
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
fun StoreScreenPreview() {
    MypatTheme {
        StoreScreen(
            onDialogCloseClick = {},
            onPatRoomUpClick = {},
            onSimpleDialog = {},
            onItemRoomUpClick = {},
            onNameTextChange = {},
            changeShowDialog = {},
            onNameChangeConfirm = {},
            onNameChangeClick = {},
            onMoneyChangeClick = {},
            onPatStoreClick = {},
            onPatEggClick = {},
            onPatSelectClick = {},
            onItemClick = {},
            onItemStoreClick = {},
            onItemSelectClick = {},
            onItemSelectCloseClick = {},

            newPat = null,
            userData = emptyList(),
            showDialog = "",
            simpleDialogState = "",
            newArea = null,
            newItem = null,
            newName = "",
            patEggDataList = emptyList(),
            patStoreDataList = emptyList(),
            patSelectIndexList = emptyList(),
            selectPatData = null,
            shuffledItemDataList = emptyList(),
            selectItemData = null,
            selectAreaData = null

        )
    }
}
