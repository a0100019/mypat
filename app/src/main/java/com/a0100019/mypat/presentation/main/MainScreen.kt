package com.a0100019.mypat.presentation.main


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
import com.a0100019.mypat.presentation.main.mainDialog.WorldAddDialog
import com.a0100019.mypat.presentation.main.management.ManagementViewModel
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import com.a0100019.mypat.presentation.world.WorldScreen
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
        onWorldChangeClick = mainViewModel::onWorldChangeClick,
        onWorldSelectClick = mainViewModel::onWorldSelectClick,
        loadData = mainViewModel::loadData,
        onShowAddDialogClick = mainViewModel::onShowAddDialogClick,
        onAddDialogChangeClick = mainViewModel::onAddDialogChangeClick,
        onSelectMapImageClick = mainViewModel::onSelectMapImageClick,
        onAddPatClick = mainViewModel::onAddPatClick,
        onAddItemClick = mainViewModel::onAddItemClick,

        mapUrl = mainState.mapData.value,
        patDataList = mainState.patDataList,
        itemDataList = mainState.itemDataList,
        dialogPatId = mainState.dialogPatId,
        worldChange = mainState.worldChange,
        showWorldAddDialog = mainState.showWorldAddDialog,
        userFlowDataList = mainState.userFlowDataList,
        addDialogChange = mainState.addDialogChange,
        mapWorldData = it,
        allMapDataList = mainState.allMapDataList,
        patFlowWorldDataList = mainState.patFlowWorldDataList,
        worldDataList = mainState.worldDataList,
        userDataList = mainState.userDataList

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
    onWorldChangeClick: () -> Unit,
    onWorldSelectClick: () -> Unit,
    loadData: () -> Unit,
    onShowAddDialogClick: () -> Unit,
    onAddDialogChangeClick: () -> Unit,
    onSelectMapImageClick: (String) -> Unit,
    onAddPatClick: (String) -> Unit,
    onAddItemClick: (String) -> Unit,

    mapUrl: String,
    patDataList: List<Pat>,
    itemDataList: List<Item>,
    dialogPatId: String,
    worldChange: Boolean,
    showWorldAddDialog: Boolean,
    userFlowDataList: Flow<List<User>>,
    addDialogChange: String,
    mapWorldData: World,
    allMapDataList: List<Item>,
    patFlowWorldDataList: Flow<List<Pat>>,
    worldDataList: List<World>,
    userDataList: List<User>,

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
                    allPatDataList = patDataList,
                    allItemDataList = itemDataList,
                    addDialogChange = addDialogChange,
                    onAddDialogChangeClick = onAddDialogChangeClick,
                    onSelectMapImageClick = onSelectMapImageClick,
                    mapWorldData = mapWorldData,
                    allMapDataList = allMapDataList,
                    worldDataList = worldDataList,
                    onAddItemClick = onAddItemClick,
                    onAddPatClick = onAddPatClick
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

                    val users by userFlowDataList.collectAsState(initial = emptyList())
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
                        Text("Pat ${userDataList.find { it.id == "pat" }?.value3} / ${userDataList.find { it.id == "pat" }?.value2}  " +
                                "Item ${userDataList.find { it.id == "item" }?.value3} / ${userDataList.find { it.id == "item" }?.value2}")
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


                WorldViewScreen(
                    mapUrl = mapUrl,
                    patDataList = patDataList,
                    itemDataList = itemDataList,
                    dialogPatId = dialogPatId,
                    dialogPatIdChange = dialogPatIdChange,
                    onFirstGameNavigateClick = onFirstGameNavigateClick,
                    onSecondGameNavigateClick = onSecondGameNavigateClick,
                    onThirdGameNavigateClick = onThirdGameNavigateClick,
                    patFlowWorldDataList = patFlowWorldDataList,
                    worldDataList = worldDataList,
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
            itemDataList = listOf(Item(url = "item/table.png")),
            dialogPatId = "0",
            dialogPatIdChange = { },
            onWorldChangeClick = {},
            worldChange = false,
            onWorldSelectClick = {},
            loadData = {},
            showWorldAddDialog = false,
            onShowAddDialogClick = {},
            userFlowDataList = flowOf(listOf(User(id = "money", value = "1000"), User(id = "cash", value = "100"))),
            onAddDialogChangeClick = {},
            addDialogChange = "map",
            allMapDataList = listOf(Item(url = "item/table.png")),
            mapWorldData = World(id = 1),
            onSelectMapImageClick = {},
            patFlowWorldDataList = flowOf(emptyList()),
            worldDataList = emptyList(),
            onAddItemClick = {},
            onAddPatClick = {},
            userDataList = emptyList(),
        )
    }
}