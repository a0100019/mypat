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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.main.world.WorldScreen
import com.a0100019.mypat.presentation.main.world.WorldSideEffect
import com.a0100019.mypat.presentation.main.world.WorldState
import com.a0100019.mypat.presentation.main.world.WorldViewModel
import com.a0100019.mypat.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    worldViewModel: WorldViewModel = hiltViewModel(),
    onDailyNavigateClick: () -> Unit,
    onStoreNavigateClick: () -> Unit,
    onGameNavigateClick: () -> Unit,
    onIndexNavigateClick: () -> Unit
) {


    val mainState : MainState = mainViewModel.collectAsState().value
    val worldState : WorldState = worldViewModel.collectAsState().value

    val context = LocalContext.current

    mainViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MainSideEffect.Toast ->
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()

        }
    }

    worldViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is WorldSideEffect.Toast ->
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()

        }
    }



    MainScreen(
        onDailyNavigateClick = onDailyNavigateClick,
        onGameNavigateClick = onGameNavigateClick,
        onIndexNavigateClick = onIndexNavigateClick,
        onStoreNavigateClick = onStoreNavigateClick,
        mapUrl = worldState.mapData?.value ?: "map/loading.jpg",
        firstPatData = worldState.firstPatData,
        firstPatWorldData = worldState.firstPatWorldData,
        firstItemData = worldState.firstItemData,
        firstItemWorldData = worldState.firstItemWorldData
    )

}

@Composable
fun MainScreen(
    onDailyNavigateClick: () -> Unit,
    onStoreNavigateClick: () -> Unit,
    onGameNavigateClick: () -> Unit,
    onIndexNavigateClick: () -> Unit,
    mapUrl: String,
    firstPatData: Pat,
    firstPatWorldData: World,
    firstItemData: Item,
    firstItemWorldData: World

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

            WorldScreen(
                mapUrl = mapUrl,
                firstPatData = firstPatData,
                firstPatWorldData = firstPatWorldData,
                firstItemData = firstItemData,
                firstItemWorldData = firstItemWorldData
            )

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
                        onClick = onGameNavigateClick
                    ) {
                        Text("게임")
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MypatTheme {
        MainScreen(
            onDailyNavigateClick = {},
            onGameNavigateClick = {},
            onIndexNavigateClick = {},
            onStoreNavigateClick = {},
            mapUrl = "map/forest.jpg",
            firstPatData = Pat(url = "pat/cat.json"),
            firstPatWorldData = World(id = "pat1"),
            firstItemData = Item(url = "item/table.png"),
            firstItemWorldData = World(id = "item1")
        )
    }
}