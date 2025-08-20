package com.a0100019.mypat.presentation.main


import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.ui.MusicPlayer
import com.a0100019.mypat.presentation.main.mainDialog.LovePatDialog
import com.a0100019.mypat.presentation.main.management.ManagementViewModel
import com.a0100019.mypat.presentation.setting.LetterViewDialog
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import com.a0100019.mypat.presentation.main.world.WorldViewScreen
import com.a0100019.mypat.presentation.ui.component.MainButton
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
    onWorldNavigateClick: () -> Unit,
    onFirstGameNavigateClick: () -> Unit,
    onSecondGameNavigateClick: () -> Unit,
    onThirdGameNavigateClick: () -> Unit,

    ) {

    val mainState : MainState = mainViewModel.collectAsState().value

    val context = LocalContext.current

    //뷰모델 거치는 navigate만 여기 작성
    mainViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MainSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            is MainSideEffect.OpenUrl -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sideEffect.url))
                context.startActivity(intent)
            }
        }
    }

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
        onWorldNavigateClick = onWorldNavigateClick,

        onLetterReadClick = mainViewModel::onLetterReadClick,
        onLetterLinkClick = mainViewModel::onLetterLinkClick,
        dialogPatIdChange = mainViewModel::dialogPatIdChange,
        onSituationChange = mainViewModel::onSituationChange,
        onLovePatChange = mainViewModel::onLovePatChange,
        onLoveItemDrag = mainViewModel::onLoveItemDrag,
        onLovePatNextClick = mainViewModel::onLovePatNextClick,
        onLovePatStopClick = mainViewModel::onLovePatStopClick,

        mapUrl = mainState.mapData.value,
        patDataList = mainState.patDataList,
        itemDataList = mainState.itemDataList,
        dialogPatId = mainState.dialogPatId,
        userFlowDataList = mainState.userFlowDataList,
        patFlowWorldDataList = mainState.patFlowWorldDataList,
        worldDataList = mainState.worldDataList,
        userDataList = mainState.userDataList,
        showLetterData = mainState.showLetterData,
        situation = mainState.situation,
        lovePatData = mainState.lovePatData,
        loveItemData1 = mainState.loveItemData1,
        loveItemData2 = mainState.loveItemData2,
        loveItemData3 = mainState.loveItemData3,
        loveAmount = mainState.loveAmount,
        cashAmount = mainState.cashAmount,
        timer = mainState.timer

    )

}

@Composable
fun MainScreen(
    onDailyNavigateClick: () -> Unit,
    onStoreNavigateClick: () -> Unit,
    onIndexNavigateClick: () -> Unit,
    onSettingNavigateClick: () -> Unit,
    onWorldNavigateClick: () -> Unit,
    onInformationNavigateClick: () -> Unit,
    onCommunityNavigateClick: () -> Unit,
    onFirstGameNavigateClick: () -> Unit,
    onSecondGameNavigateClick: () -> Unit,
    onThirdGameNavigateClick: () -> Unit,

    dialogPatIdChange: (String) -> Unit,
    onLetterReadClick: () -> Unit = {},
    onLetterLinkClick: () -> Unit,
    onSituationChange: (String) -> Unit,
    onLovePatChange: (Int) -> Unit,
    onLoveItemDrag: (String, Float, Float) -> Unit,
    onLovePatNextClick: () -> Unit,
    onLovePatStopClick: () -> Unit,

    mapUrl: String,
    patDataList: List<Pat>,
    itemDataList: List<Item>,
    dialogPatId: String,
    userFlowDataList: Flow<List<User>>,
    patFlowWorldDataList: Flow<List<Pat>>,
    worldDataList: List<World>,
    userDataList: List<User>,
    showLetterData: Letter,
    situation: String,
    lovePatData: Pat,
    loveItemData1: Item,
    loveItemData2: Item,
    loveItemData3: Item,
    loveAmount: Int,
    cashAmount: Int = 0,
    timer: String

    ) {

    when(situation) {
        "letter" -> LetterViewDialog(
            onClose = {},
            onLetterLinkClick = onLetterLinkClick,
            onLetterConfirmClick = onLetterReadClick,
            clickLetterData = showLetterData,
            closeVisible = false
        )
    }

    if(lovePatData.id != 0) {
        LovePatDialog(
            lovePatData = lovePatData,
            loveItemData1 = loveItemData1,
            loveItemData2 = loveItemData2,
            loveItemData3 = loveItemData3,
            onItemDrag = onLoveItemDrag,
            situation = situation,
            onLovePatNextClick = onLovePatNextClick,
            onLovePatStopClick = onLovePatStopClick,
            loveAmount = loveAmount,
            cashAmount = cashAmount
        )
    }

    Surface {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ){

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    MainButton(
                        text = "내 정보",
//                        iconResId = R.drawable.information,
                        onClick = onInformationNavigateClick,
                    )

                    val users by userFlowDataList.collectAsState(initial = emptyList())
                    Row(
                        modifier = Modifier
//                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)) // 💥 그림자 추가
                            .background(
                                color = MaterialTheme.colorScheme.scrim,
                                shape = RoundedCornerShape(16.dp)
                            ) // ✨ 배경 추가
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                        ,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "💰",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = users.find { it.id == "money" }?.value ?: "0",
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Spacer(modifier = Modifier.size(12.dp))

                        Text(
                            text = "💸",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = users.find { it.id == "money" }?.value2 ?: "0",
                            style = MaterialTheme.typography.bodyLarge,
                        )

                    }

                    Row(
//                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MainButton(
                            onClick = onSettingNavigateClick,
                            text = "설정"
                        )
                    }

                }

            }

            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        JustImage(
                            filePath = "etc/loveBubble.json",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            text = timer
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    //편지 이미지
                    if(showLetterData.id != 0){
                        JustImage(
                            filePath = "etc/letter.json",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        onSituationChange("letter")
                                    }
                                )
                        )
                    }
                    MainButton(
                        text = "커뮤니티",
                        onClick = onCommunityNavigateClick
                    )
                    MainButton(
                        text = "꾸미기 모드",
                        onClick = onWorldNavigateClick
                    )
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
                    onLovePatChange = onLovePatChange
                )

            }

            Column(

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    MainButton(
                        text = "일일 루틴",
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .padding(bottom = 10.dp),
                        onClick = onDailyNavigateClick,
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(10.dp) // 버튼 간 간격
                ) {

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .weight(0.4f)
                    ){
                        MainButton(
                            text = "상점",
                            modifier = Modifier
                                .fillMaxWidth(0.6f),
                            onClick = onStoreNavigateClick
                        )

                    }

                    Spacer(modifier = Modifier.weight(0.2f))

                    Row(
                        modifier = Modifier
                            .weight(0.4f)
                    ){
                        MainButton(
                            text = "도감",
                            modifier = Modifier
                                .fillMaxWidth(0.6f),
                            onClick = onIndexNavigateClick
                        )
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
            onWorldNavigateClick = {},
            onCommunityNavigateClick = {},
            onSettingNavigateClick = {},
            onFirstGameNavigateClick = {},
            onSecondGameNavigateClick = {},
            onThirdGameNavigateClick = {},
            mapUrl = "area/forest.jpg",
            patDataList = listOf(Pat(url = "pat/cat.json")),
            itemDataList = listOf(Item(url = "item/table.png")),
            dialogPatId = "0",
            dialogPatIdChange = {},
            userFlowDataList = flowOf(listOf(User(id = "money", value = "1000"), User(id = "cash", value = "100"))),
            patFlowWorldDataList = flowOf(emptyList()),
            worldDataList = emptyList(),
            userDataList = emptyList(),
            onSituationChange = {},
            onLetterLinkClick = {},
            situation = "",
            showLetterData = Letter(id = 1),
            onLovePatChange = {},
            lovePatData = Pat(url = "pat/cat.json"),
            onLoveItemDrag = { id, newX, newY -> },
            loveItemData1 = Item(id = 1, name = "쓰다듬기", url = "etc/hand.png", x = 0.2f, y = 0.7f, sizeFloat = 0.2f),
            loveItemData2 = Item(id = 2, name = "장난감", url = "etc/arrow.png", x = 0.5f, y = 0.7f, sizeFloat = 0.2f),
            loveItemData3 = Item(id = 3, name = "비행기", url = "etc/lock.png", x = 0.8f, y = 0.7f, sizeFloat = 0.2f),
            loveAmount = 100,
            onLovePatNextClick = {},
            onLovePatStopClick = {},
            timer = "11:00"


        )
    }
}