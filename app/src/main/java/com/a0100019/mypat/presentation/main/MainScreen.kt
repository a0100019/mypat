package com.a0100019.mypat.presentation.main


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.domain.AppBgmManager
import com.a0100019.mypat.presentation.daily.walk.StepForegroundService
import com.a0100019.mypat.presentation.main.mainDialog.LovePatDialog
import com.a0100019.mypat.presentation.main.mainDialog.TutorialDialog
import com.a0100019.mypat.presentation.main.management.ManagementViewModel
import com.a0100019.mypat.presentation.setting.LetterViewDialog
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import com.a0100019.mypat.presentation.main.world.WorldViewScreen
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
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
    onSettingNavigateClick: () -> Unit,
    onWorldNavigateClick: () -> Unit,
    onFirstGameNavigateClick: () -> Unit,
    onSecondGameNavigateClick: () -> Unit,
    onThirdGameNavigateClick: () -> Unit,
    onOperatorNavigateClick: () -> Unit,
    onPrivateRoomNavigateClick: () -> Unit,
    onNeighborNavigateClick: () -> Unit = {}

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

    // 화면이 다시 포그라운드가 될 때마다 실행
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // 화면이 다시 나타날 때마다 호출
                mainViewModel.checkNewMessage()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    MainScreen(

        onDailyNavigateClick = onDailyNavigateClick,
        onIndexNavigateClick = onIndexNavigateClick,
        onStoreNavigateClick = onStoreNavigateClick,
        onSettingNavigateClick = onSettingNavigateClick,
        onInformationNavigateClick = onInformationNavigateClick,
        onFirstGameNavigateClick = onFirstGameNavigateClick,
        onSecondGameNavigateClick = onSecondGameNavigateClick,
        onThirdGameNavigateClick = onThirdGameNavigateClick,
        onWorldNavigateClick = onWorldNavigateClick,
        onOperatorNavigateClick = onOperatorNavigateClick,
        onPrivateRoomNavigateClick = onPrivateRoomNavigateClick,
        onNeighborNavigateClick = onNeighborNavigateClick,

        onLetterReadClick = mainViewModel::onLetterReadClick,
        onLetterLinkClick = mainViewModel::onLetterLinkClick,
        dialogPatIdChange = mainViewModel::dialogPatIdChange,
        onSituationChange = mainViewModel::onSituationChange,
        onLovePatChange = mainViewModel::onLovePatChange,
        onLoveItemClick = mainViewModel::onLoveItemClick,
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
        timer = mainState.timer,
        itemDataWithShadowList = mainState.itemDataWithShadowList,
        musicTrigger = mainState.musicTrigger,
        newMessage = mainState.newMessage

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
    onFirstGameNavigateClick: () -> Unit,
    onSecondGameNavigateClick: () -> Unit,
    onThirdGameNavigateClick: () -> Unit,
    onNeighborNavigateClick: () -> Unit = {},
    onOperatorNavigateClick: () -> Unit = {},
    onPrivateRoomNavigateClick: () -> Unit = {},

    dialogPatIdChange: (String) -> Unit,
    onLetterReadClick: () -> Unit = {},
    onLetterLinkClick: () -> Unit,
    onSituationChange: (String) -> Unit,
    onLovePatChange: (Int) -> Unit,
    onLoveItemClick: (String) -> Unit,
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
    timer: String,
    itemDataWithShadowList: List<Item> = emptyList(),
    musicTrigger: Int = 0,
    newMessage: Boolean = false

    ) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("bgm_prefs", Context.MODE_PRIVATE)
    val bgm = prefs.getString("bgm", "area/forest.jpg")

    val intent = Intent(context, StepForegroundService::class.java)
    context.stopService(intent)

    if(bgm != mapUrl) {
        //노래 변경
        AppBgmManager.init(context, mapUrl) // ✅ context 전달
        prefs.edit().putString("bgm", mapUrl).apply()
    }

    val tutorialPrefs = context.getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
    val tutorial = tutorialPrefs.getString("tutorial", "미션")
    var tutorialText by remember { mutableStateOf("진행") }

    if(tutorialText == "진행"){
        when (tutorial) {

            "미션" -> TutorialDialog(
                state = "미션",
                onDailyClick = {
                    tutorialText = "완료"
                    onDailyNavigateClick()
                    tutorialPrefs.edit().putString("tutorial", "커뮤니티").apply()
                }
            )

            "커뮤니티" -> TutorialDialog(
                state = "커뮤니티",
                onChatClick = {
                    tutorialText = "완료"
                    onNeighborNavigateClick()
                    tutorialPrefs.edit().putString("tutorial", "상점").apply()
                }
            )

            "상점" -> TutorialDialog(
                state = "상점",
                onStoreClick = {
                    tutorialText = "완료"
                    onStoreNavigateClick()
                    tutorialPrefs.edit().putString("tutorial", "꾸미기").apply()
                }
            )

            "꾸미기" -> TutorialDialog(
                state = "꾸미기",
                onWorldClick = {
                    tutorialText = "완료"
                    onWorldNavigateClick()
                    tutorialPrefs.edit().putString("tutorial", "완료").apply()
                }
            )
        }
    }

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
            onItemClick = onLoveItemClick,
            situation = situation,
            onLovePatNextClick = onLovePatNextClick,
            onLovePatStopClick = onLovePatStopClick,
            loveAmount = loveAmount,
            cashAmount = cashAmount,
            musicTrigger = musicTrigger
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

//        MusicPlayer(
//            id = R.raw.bgm_positive
//        )

        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ){

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    MainButton(
                        text = "프로필",
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

                        JustImage(
                            filePath = "etc/sun.png",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 3.dp)
                        )

                        Text(
                            text = users.find { it.id == "money" }?.value ?: "0",
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Spacer(modifier = Modifier.size(16.dp))

                        JustImage(
                            filePath = "etc/moon.png",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 3.dp)
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
                    .padding(16.dp)
                    .weight(1f)
                ,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
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

                    if(newMessage) {
                        JustImage(
                            filePath = "etc/letter.json",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        onPrivateRoomNavigateClick()
                                    }
                                )
                        )
                    }

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
                        text = "상점",
                        onClick = onStoreNavigateClick
                    )
                    MainButton(
                        text = "도감",
                        onClick = onIndexNavigateClick
                    )
                    MainButton(
                        text = "꾸미기",
                        onClick = onWorldNavigateClick
                    )
                }

                WorldViewScreen(
                    mapUrl = mapUrl,
                    patDataList = patDataList,
                    itemDataList = itemDataWithShadowList,
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
                modifier = Modifier
                    .weight(0.25f)
                    .padding(12.dp)
                ,
                verticalArrangement = Arrangement.Bottom
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.96f else 1f,
                    label = "daily_mission_scale"
                )

// ✨ 반짝임 애니메이션
                val shimmerX by rememberInfiniteTransition(label = "shimmer").animateFloat(
                    initialValue = -0.4f,
                    targetValue = 1.4f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 2200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "shimmerX"
                )

                val shimmerColor = Color.White.copy(alpha = 0.35f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onDailyNavigateClick()
                            },
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFEAF4F1),
                        border = BorderStroke(1.5.dp, Color(0xFF9ECFC3))
                    ) {
                        Box {

                            // 🌿 버튼 내용
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 16.dp, horizontal = 16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                    Text(
                                        text = "✨ 하루 미션 ✨",
                                        style = MaterialTheme.typography.titleLarge, // 🔥 한 단계 키움
                                        color = Color(0xFF2F6F62)
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "오늘의 작은 성장 기록",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF6FA9A0)
                                    )
                                }

                            }

                            // ✨ 반짝임 레이어 (유리 스윕 효과)
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colorStops = arrayOf(
                                                (shimmerX - 0.18f) to Color.Transparent,
                                                shimmerX to shimmerColor,
                                                (shimmerX + 0.18f) to Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }



                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                    MainButton(
                        text = "친구",
                        modifier = Modifier
                            .fillMaxWidth(0.4f),
                        onClick = onPrivateRoomNavigateClick
                    )

                    if(userDataList.find { it.id == "auth" }?.value2 ?: "" in listOf("1", "38", "75", "181") ) {
                        MainButton(
                            text = "관리자",
                            modifier = Modifier
                                .fillMaxWidth(0.4f),
                            onClick = onOperatorNavigateClick
                        )
                    }

                    MainButton(
                        text = "커뮤니티",
                        modifier = Modifier
                            .fillMaxWidth(0.66f),
                        onClick = onNeighborNavigateClick
                    )
                    
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
            onSettingNavigateClick = {},
            onFirstGameNavigateClick = {},
            onSecondGameNavigateClick = {},
            onThirdGameNavigateClick = {},
            mapUrl = "area/forest.jpg",
            patDataList = listOf(Pat(url = "pat/cat.json")),
            itemDataList = listOf(Item(url = "item/airplane.json")),
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
            onLoveItemClick = { },
            loveItemData1 = Item(id = 1, name = "쓰다듬기", url = "etc/toy_car.png", x = 0.2f, y = 0.7f, sizeFloat = 0.2f),
            loveItemData2 = Item(id = 2, name = "장난감", url = "etc/toy_lego.png", x = 0.5f, y = 0.7f, sizeFloat = 0.2f),
            loveItemData3 = Item(id = 3, name = "비행기", url = "etc/toy_bear.png", x = 0.8f, y = 0.7f, sizeFloat = 0.2f),
            loveAmount = 100,
            onLovePatNextClick = {},
            onLovePatStopClick = {},
            timer = "11:00"


        )
    }
}