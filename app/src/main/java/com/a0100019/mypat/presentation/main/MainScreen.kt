package com.a0100019.mypat.presentation.main


import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.daily.walk.WalkViewModel
import com.a0100019.mypat.presentation.main.mainDialog.UserInformationDialog
import com.a0100019.mypat.presentation.main.mainDialog.WorldAddDialog
import com.a0100019.mypat.presentation.main.management.ManagementViewModel
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    managementViewModel: ManagementViewModel = hiltViewModel(),
    onDailyNavigateClick: () -> Unit,
    onStoreNavigateClick: () -> Unit,
    onIndexNavigateClick: () -> Unit,
    onInformationNavigateClick: () -> Unit,
    onCommunityNavigateClick: () -> Unit,
    onSettingNavigateClick: () -> Unit,
    onFirstGameNavigateClick: () -> Unit,
    onSecondGameNavigateClick: () -> Unit,
    onThirdGameNavigateClick: () -> Unit,


    ) {


    val mainState : MainState = mainViewModel.collectAsState().value

    val context = LocalContext.current

    mainViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MainSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()

        }
    }




    mainState.mapData?.let {
        MainScreen(
        onDailyNavigateClick = onDailyNavigateClick,
        onIndexNavigateClick = onIndexNavigateClick,
        onStoreNavigateClick = onStoreNavigateClick,
        onSettingNavigateClick = onSettingNavigateClick,
        onInformationNavigateClick = onInformationNavigateClick,
        onCommunityNavigateClick = onCommunityNavigateClick,
        onFirstGameNavigateClick = onFirstGameNavigateClick,
        onSecondGameNavigateClick = onSecondGameNavigateClick,
        onThirdGameNavigateClick = onThirdGameNavigateClick,

        dialogPatIdChange = mainViewModel::dialogPatIdChange,
        dialogItemIdChange = mainViewModel::dialogItemIdChange,
        onWorldChangeClick = mainViewModel::onWorldChangeClick,
        onWorldSelectClick = mainViewModel::onWorldSelectClick,
        loadData = mainViewModel::loadData,
        patWorldDataDelete = mainViewModel::patWorldDataDelete,
        itemWorldDataDelete = mainViewModel::itemWorldDataDelete,
        onPatSizeUpClick = mainViewModel::onPatSizeUpClick,
        onItemSizeUpClick = mainViewModel::onItemSizeUpClick,
        onPatSizeDownClick = mainViewModel::onPatSizeDownClick,
        onItemSizeDownClick = mainViewModel::onItemSizeDownClick,
        onShowAddDialogClick = mainViewModel::onShowAddDialogClick,
        onAddPatImageClick = mainViewModel::onAddPatImageClick,
        onItemDrag = mainViewModel::onItemDrag,
        onPatDrag = mainViewModel::onPatDrag,
        onAddDialogChangeClick = mainViewModel::onAddDialogChangeClick,
        onAddItemImageClick = mainViewModel::onAddItemImageClick,
        onSelectMapImageClick = mainViewModel::onSelectMapImageClick,


        mapUrl = mainState.mapData.value,
        patDataList = mainState.patDataList,
        patWorldDataList = mainState.patWorldDataList,
        itemDataList = mainState.itemDataList,
        itemWorldDataList = mainState.itemWorldDataList,
        dialogPatId = mainState.dialogPatId,
        dialogItemId = mainState.dialogItemId,
        worldChange = mainState.worldChange,
        showWorldAddDialog = mainState.showWorldAddDialog,
        allPatDataList = mainState.allPatDataList,
        allItemDataList = mainState.allItemDataList,
        userDataList = mainState.userDataList,
        addDialogChange = mainState.addDialogChange,
        mapWorldData = it,
        allMapDataList = mainState.allMapDataList,
        patFlowWorldDataList = mainState.patFlowWorldDataList

    )
    }

}

@Composable
fun MainScreen(
    onDailyNavigateClick: () -> Unit,
    onStoreNavigateClick: () -> Unit,
    onIndexNavigateClick: () -> Unit,
    onSettingNavigateClick: () -> Unit,
    onInformationNavigateClick: () -> Unit,
    onCommunityNavigateClick: () -> Unit,
    onFirstGameNavigateClick: () -> Unit,
    onSecondGameNavigateClick: () -> Unit,
    onThirdGameNavigateClick: () -> Unit,

    dialogPatIdChange: (String) -> Unit,
    dialogItemIdChange: (String) -> Unit,
    onWorldChangeClick: () -> Unit,
    onWorldSelectClick: () -> Unit,
    loadData: () -> Unit,
    patWorldDataDelete: (String) -> Unit,
    itemWorldDataDelete: (String) -> Unit,
    onPatSizeUpClick: () -> Unit,
    onItemSizeUpClick: () -> Unit,
    onPatSizeDownClick: () -> Unit,
    onItemSizeDownClick: () -> Unit,
    onShowAddDialogClick: () -> Unit,
    onAddPatImageClick: (String) -> Unit,
    onItemDrag: (String, Float, Float) -> Unit,
    onPatDrag: (String, Float, Float) -> Unit,
    onAddDialogChangeClick: () -> Unit,
    onAddItemImageClick: (String) -> Unit,
    onSelectMapImageClick: (String) -> Unit,

    mapUrl: String,
    patDataList: List<Pat>,
    patWorldDataList: List<World>,
    itemDataList: List<Item>,
    itemWorldDataList: List<World>,
    dialogPatId: String,
    dialogItemId: String,
    worldChange: Boolean,
    showWorldAddDialog: Boolean,
    allPatDataList: List<Pat>,
    allItemDataList: List<Item>,
    userDataList: Flow<List<User>>,
    addDialogChange: String,
    mapWorldData: World,
    allMapDataList: List<Item>,
    patFlowWorldDataList: Flow<List<Pat>>,

) {

    Surface {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ){

            // 다이얼로그 표시
            if (showWorldAddDialog) {
                WorldAddDialog(
                    onClose = onShowAddDialogClick,
                    allPatDataList = allPatDataList,
                    patWorldDataList = patWorldDataList,
                    allItemDataList = allItemDataList,
                    itemWorldDataList = itemWorldDataList,
                    onAddPatImageClick = onAddPatImageClick,
                    addDialogChange = addDialogChange,
                    onAddDialogChangeClick = onAddDialogChangeClick,
                    onAddItemImageClick = onAddItemImageClick,
                    onSelectMapImageClick = onSelectMapImageClick,
                    mapWorldData = mapWorldData,
                    allMapDataList = allMapDataList
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(!worldChange){
                    Button(
                        onClick = onInformationNavigateClick
                    ) {
                        Text("내 정보")
                    }

                    val users by userDataList.collectAsState(initial = emptyList())
                    Text("money : ${users.find { it.id == "money" }?.value} | cash : ${users.find { it.id == "money" }?.value2}")

                    Button(
                        onClick = onSettingNavigateClick
                    ) {
                        Text("설정")
                    }
                }
            }

            Column {
                if(worldChange) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text("Pat ${patWorldDataList.count { it.value != "0" }} / ${patWorldDataList.count { it.open == "1" }}  " +
                                "Item ${itemWorldDataList.count { it.value != "0" }} / ${itemWorldDataList.count { it.open == "1" }}")
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onCommunityNavigateClick
                        ) {
                            Text("커뮤니티")
                        }
                        Button(
                            onClick = onWorldChangeClick
                        ) {
                            Text("꾸미기 모드")
                        }
                    }
                }

                WorldScreen(
                    mapUrl = mapUrl,
                    patDataList = patDataList,
                    patWorldDataList = patWorldDataList,
                    itemDataList = itemDataList,
                    itemWorldDataList = itemWorldDataList,
                    dialogPatId = dialogPatId,
                    dialogItemId = dialogItemId,
                    dialogPatIdChange = dialogPatIdChange,
                    dialogItemIdChange = dialogItemIdChange,
                    onFirstGameNavigateClick = onFirstGameNavigateClick,
                    onSecondGameNavigateClick = onSecondGameNavigateClick,
                    onThirdGameNavigateClick = onThirdGameNavigateClick,
                    worldChange = worldChange,
                    patWorldDataDelete = patWorldDataDelete,
                    itemWorldDataDelete = itemWorldDataDelete,
                    onPatSizeDownClick = onPatSizeDownClick,
                    onItemSizeDownClick = onItemSizeDownClick,
                    onPatSizeUpClick = onPatSizeUpClick,
                    onItemSizeUpClick = onItemSizeUpClick,
                    onItemDrag = onItemDrag,
                    onPatDrag = onPatDrag,
                    patFlowWorldDataList = patFlowWorldDataList
                )
            }

            if(worldChange) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(0.5f),
                            onClick = onShowAddDialogClick
                        ) {
                            Text("추가 하기")
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            modifier = Modifier,
                            onClick = {
                                onWorldChangeClick()
                                loadData()
                            }
                        ) {
                            Text("취소")
                        }
                        Button(
                            modifier = Modifier,
                            onClick = {
                                onWorldSelectClick()
                                onWorldChangeClick()
                                loadData()
                            }
                        ) {
                            Text("확인")
                        }
                    }

                }
            } else {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(0.5f),
                            onClick = onDailyNavigateClick
                        ) {
                            Text("일일 루틴")
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            modifier = Modifier,
                            onClick = onStoreNavigateClick
                        ) {
                            Text("상점")
                        }
                        Button(
                            modifier = Modifier,
                            onClick = onIndexNavigateClick
                        ) {
                            Text("도감")
                        }
                    }

                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MypatTheme {
        MainScreen(
            onDailyNavigateClick = {},
            onIndexNavigateClick = {},
            onStoreNavigateClick = {},
            onInformationNavigateClick = {},
            onCommunityNavigateClick = {},
            onSettingNavigateClick = {},
            onFirstGameNavigateClick = {},
            onSecondGameNavigateClick = {},
            onThirdGameNavigateClick = {},
            mapUrl = "map/forest.jpg",
            patDataList = listOf(Pat(url = "pat/cat.json")),
            patWorldDataList = listOf(World(id = "pat1")),
            itemDataList = listOf(Item(url = "item/table.png")),
            itemWorldDataList = listOf(World(id = "item1")),
            dialogPatId = "0",
            dialogItemId = "0",
            dialogPatIdChange = { },
            dialogItemIdChange = {},
            onWorldChangeClick = {},
            worldChange = false,
            onWorldSelectClick = {},
            loadData = {},
            patWorldDataDelete = {},
            itemWorldDataDelete = {},
            onItemSizeUpClick = {},
            onPatSizeUpClick = {},
            onItemSizeDownClick = {},
            onPatSizeDownClick = {},
            showWorldAddDialog = false,
            onShowAddDialogClick = {},
            allPatDataList = listOf(Pat(url = "pat/cat.json")),
            onAddPatImageClick = {},
            userDataList = flowOf(listOf(User(id = "money", value = "1000"), User(id = "cash", value = "100"))),
            onItemDrag = { id, newX, newY -> },
            onPatDrag = { id, newX, newY -> },
            allItemDataList = listOf(Item(url = "item/table.png")),
            onAddDialogChangeClick = {},
            addDialogChange = "map",
            onAddItemImageClick = {},
            allMapDataList = listOf(Item(url = "item/table.png")),
            mapWorldData = World(id = "1"),
            onSelectMapImageClick = {},
            patFlowWorldDataList = flowOf(emptyList()),
        )
    }
}