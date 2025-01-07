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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.game.firstGame.FirstGameActivity
import com.a0100019.mypat.presentation.game.secondGame.SecondGameActivity
import com.a0100019.mypat.presentation.game.thirdGame.ThirdGameActivity
import com.a0100019.mypat.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    onDailyNavigateClick: () -> Unit,
    onStoreNavigateClick: () -> Unit,
    onIndexNavigateClick: () -> Unit
) {


    val mainState : MainState = mainViewModel.collectAsState().value

    val context = LocalContext.current

    mainViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MainSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            MainSideEffect.FirstGameActivity -> {
                context.startActivity(Intent(context, FirstGameActivity::class.java))
            }
            MainSideEffect.SecondGameActivity -> {
                context.startActivity(Intent(context, SecondGameActivity::class.java))
            }
            MainSideEffect.ThirdGameActivity -> {
                context.startActivity(Intent(context, ThirdGameActivity::class.java))
            }
        }
    }




    MainScreen(
        onDailyNavigateClick = onDailyNavigateClick,
        onIndexNavigateClick = onIndexNavigateClick,
        onStoreNavigateClick = onStoreNavigateClick,

        dialogPatIdChange = mainViewModel::dialogPatIdChange,
        onFirstGameClick = mainViewModel::onFirstGameClick,
        onSecondGameClick = mainViewModel::onSecondGameClick,
        onThirdGameClick = mainViewModel::onThirdGameClick,
        onWorldChangeClick = mainViewModel::onWorldChangeClick,
        onWorldSelectClick = mainViewModel::onWorldSelectClick,
        loadData = mainViewModel::loadData,
        patWorldDataDelete = mainViewModel::patWorldDataDelete,

        mapUrl = mainState.mapData?.value ?: "map/loading.jpg",
        patDataList = mainState.patDataList,
        patWorldDataList = mainState.patWorldDataList,
        itemDataList = mainState.itemDataList,
        itemWorldDataList = mainState.itemWorldDataList,
        dialogPatId = mainState.dialogPatId,
        worldChange = mainState.worldChange

    )

}

@Composable
fun MainScreen(
    onDailyNavigateClick: () -> Unit,
    onStoreNavigateClick: () -> Unit,
    onIndexNavigateClick: () -> Unit,

    dialogPatIdChange : (String) -> Unit,
    onFirstGameClick: () -> Unit,
    onSecondGameClick: () -> Unit,
    onThirdGameClick: () -> Unit,
    onWorldChangeClick: () -> Unit,
    onWorldSelectClick: () -> Unit,
    loadData: () -> Unit,
    patWorldDataDelete: (String) -> Unit,

    mapUrl: String,
    patDataList: List<Pat>,
    patWorldDataList: List<World>,
    itemDataList: List<Item>,
    itemWorldDataList: List<World>,
    dialogPatId : String,
    worldChange: Boolean

) {

    Surface {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(!worldChange){
                    Button(
                        onClick = {}
                    ) {
                        Text("내 정보")
                    }
                    Button(
                        onClick = {}
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
                        Text("Pat ${patWorldDataList.count { it.value != "0" }} / ${patWorldDataList.size}  " +
                                "Item ${itemWorldDataList.count { it.value != "0" }} / ${itemWorldDataList.size}")
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onWorldChangeClick
                        ) {
                            Text("꾸미기 모드")
                        }
                        Button(
                            onClick = {}
                        ) {
                            Text("사진 찍기")
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
                    dialogPatIdChange = dialogPatIdChange,
                    onFirstGameClick = onFirstGameClick,
                    onSecondGameClick = onSecondGameClick,
                    onThirdGameClick = onThirdGameClick,
                    worldChange = worldChange,
                    patWorldDataDelete = patWorldDataDelete
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
                            onClick = {  }
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
            mapUrl = "map/forest.jpg",
            patDataList = listOf(Pat(url = "pat/cat.json")),
            patWorldDataList = listOf(World(id = "pat1")),
            itemDataList = listOf(Item(url = "item/table.png")),
            itemWorldDataList = listOf(World(id = "item1")),
            dialogPatId = "0",
            dialogPatIdChange = { },
            onFirstGameClick = {},
            onSecondGameClick = {},
            onThirdGameClick = {},
            onWorldChangeClick = {},
            worldChange = false,
            onWorldSelectClick = {},
            loadData = {},
            patWorldDataDelete = {}

        )
    }
}