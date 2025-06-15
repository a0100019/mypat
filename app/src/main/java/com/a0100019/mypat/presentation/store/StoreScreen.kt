package com.a0100019.mypat.presentation.store

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.index.IndexItemDialog
import com.a0100019.mypat.presentation.index.IndexAreaDialog
import com.a0100019.mypat.presentation.index.IndexPatDialog
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.component.CuteIconButton
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
    shuffledItemDataList: List<String>?
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
            onAdvertisementClick = {  }
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
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            Text("money : ${userData.find { it.id == "money" }?.value}")
            Text("cash : ${userData.find {it.id == "money"}?.value2}")
        }
        LazyColumn {
            item {
                CuteIconButton(
                    onClick = { onSimpleDialog("펫을 뽑으시겠습니까?") },
                    text = "펫 뽑기 100원"
                )
            }
            item {
                CuteIconButton(
                    onClick = { onSimpleDialog("아이템을 뽑으시겠습니까?")},
                    text = "아이템 뽑기"
                )
            }
            item {
                CuteIconButton(
                    onClick = { onSimpleDialog("펫 공간을 늘리겠습니까?") },
                    text = "펫 공간 늘리기 10cash"
                )
            }
            item {
                CuteIconButton(
                    onClick = { onSimpleDialog("아이템 공간을 늘리겠습니까?") },
                    text = "아이템 공간 늘리기"
                )
            }
            item {
                CuteIconButton(
                    onClick = {changeShowDialog("name")},
                    text = "닉네임 변경"
                )
            }
            item {
                CuteIconButton(
                    onClick = {onSimpleDialog("화폐를 변경하겠습니까?")},
                    text = "화폐 변경 1d -> 100s"
                )
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
