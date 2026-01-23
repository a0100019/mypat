package com.a0100019.mypat.presentation.main


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.a0100019.mypat.presentation.activity.daily.walk.StepForegroundService
import com.a0100019.mypat.presentation.diary.DiarySideEffect
import com.a0100019.mypat.presentation.login.ExplanationDialog
import com.a0100019.mypat.presentation.main.mainDialog.LovePatDialog
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.main.mainDialog.TutorialDialog
import com.a0100019.mypat.presentation.main.management.BannerAd
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
    onNeighborNavigateClick: () -> Unit = {},
    onDiaryNavigateClick: () -> Unit = {},
    onActivityNavigateClick: () -> Unit = {}

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
            MainSideEffect.NavigateToDailyScreen -> onDailyNavigateClick()
            MainSideEffect.ExitApp -> {
                (context as? Activity)?.finish()  // ✅ 안전하게 앱 종료
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
        onDiaryNavigateClick = onDiaryNavigateClick,
        onActivityNavigateClick = onActivityNavigateClick,

        onLetterReadClick = mainViewModel::onLetterReadClick,
        onLetterLinkClick = mainViewModel::onLetterLinkClick,
        dialogPatIdChange = mainViewModel::dialogPatIdChange,
        onSituationChange = mainViewModel::onSituationChange,
        onLovePatChange = mainViewModel::onLovePatChange,
        onLoveItemClick = mainViewModel::onLoveItemClick,
        onLovePatNextClick = mainViewModel::onLovePatNextClick,
        onLovePatStopClick = mainViewModel::onLovePatStopClick,
        onCloseClick = mainViewModel::onCloseClick,
        onExitClick = mainViewModel::onExitClick,

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
    onDiaryNavigateClick: () -> Unit = {},
    onActivityNavigateClick: () -> Unit = {},

    dialogPatIdChange: (String) -> Unit,
    onLetterReadClick: () -> Unit = {},
    onLetterLinkClick: () -> Unit,
    onSituationChange: (String) -> Unit,
    onLovePatChange: (Int) -> Unit,
    onLoveItemClick: (String) -> Unit,
    onLovePatNextClick: () -> Unit,
    onLovePatStopClick: () -> Unit,
    onExitClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},

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
    val bgmOn = prefs.getBoolean("bgmOn", true)

    val intent = Intent(context, StepForegroundService::class.java)
    context.stopService(intent)

    if(bgm != mapUrl) {
        //노래 변경
        AppBgmManager.init(context, mapUrl) // ✅ context 전달
        prefs.edit().putString("bgm", mapUrl).apply()
    }

    if (bgmOn) {
        AppBgmManager.play()
    }

    val explainPrefs = context.getSharedPreferences("explain_prefs", Context.MODE_PRIVATE)
    val explain = explainPrefs.getString("explain", "0")
    var explainText by remember { mutableStateOf("진행") }

    val adPrefs = context.getSharedPreferences("ad_prefs", Context.MODE_PRIVATE)

    if(explainText == "진행"){
        if (explain == "0") {

            ExplanationDialog (
                onClose = {
                    explainText = "완료"
                    explainPrefs.edit().putString("explain", "1").apply()
                    adPrefs.edit().putString("banner", "1").apply()
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
        "exit" -> SimpleAlertDialog(
            onConfirmClick = onExitClick,
            onDismissClick = onCloseClick,
            text = "하루마을을 종료하시겠습니까?",
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
                    .padding(start = 6.dp, end = 6.dp, bottom = 6.dp, top = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 10.dp, top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    JustImage(
                        filePath = "etc/cog.png",
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                onSettingNavigateClick()
                            }
                        ,
                    )

                    // 1. 아이콘 애니메이션을 위한 Transition
                    val infiniteTransition = rememberInfiniteTransition(label = "currency_anim")
                    val iconScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.15f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "iconScale"
                    )

                    val users by userFlowDataList.collectAsState(initial = emptyList())

                    // 2. 재화창 메인 디자인
                    Row(
                        modifier = Modifier
                            // 유리 효과: 반투명 배경 + 블러 느낌의 그림자
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.4f),
                                        Color.White.copy(alpha = 0.2f)
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // --- 햇살(Sun) 섹션 ---
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            JustImage(
                                filePath = "etc/sun.png",
                                modifier = Modifier
                                    .size(22.dp)
                                    .graphicsLayer { // 둥실둥실 애니메이션
                                        scaleX = iconScale
                                        scaleY = iconScale
                                    }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = users.find { it.id == "money" }?.value ?: "0",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF424242) // 가독성을 위해 진한 회색
                                )
                            )
                        }

                        // 구분선
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(14.dp)
                                .background(Color.White.copy(alpha = 0.3f))
                        )

                        // --- 달(Moon) 섹션 ---
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            JustImage(
                                filePath = "etc/moon.png",
                                modifier = Modifier
                                    .size(20.dp)
                                    .graphicsLayer {
                                        // 달은 살짝 회전하는 느낌으로 차별화
                                        rotationZ = (iconScale - 1f) * 100f
                                    }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = users.find { it.id == "money" }?.value2 ?: "0",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF424242)
                                )
                            )
                        }
                    }

                    JustImage(
                        filePath = "etc/switch.png",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                onSituationChange("exit")
                            }
                    )

                }

            }

            Column(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp)
                ,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
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

                    if(userDataList.find { it.id == "auth" }?.value2 ?: "" in listOf("1", "38", "75", "181") ) {
                        MainButton(
                            text = "관리자",
                            modifier = Modifier
                            ,
                            onClick = onOperatorNavigateClick
                        )
                    }

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

            Row(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp), // 버튼 사이 여백 확대
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- 1. 마을 관리 (따스한 베이지-옐로우 테마) ---
                Surface(
                    onClick = {
                        onActivityNavigateClick()
                        if(userDataList.find { it.id == "name" }?.value3 == "1") {
                            adPrefs.edit().putString("banner", "2").apply()
                        } else if(userDataList.find { it.id == "name" }?.value3 == "0") {
                            adPrefs.edit().putString("banner", "1").apply()
                        }
                    },
                    modifier = Modifier.weight(1f).height(100.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFFFFF9C4), // 연노랑
                    border = BorderStroke(2.5.dp, Color(0xFFFFD54F)),
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏠", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "마을 관리",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color(0xFF795548)
                        )
                    }
                }

                // --- 2. 내 일기 (포근한 핑크-라벤더 테마) ---
                Surface(
                    onClick = { onDiaryNavigateClick() },
                    modifier = Modifier.weight(1f).height(100.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFFFCE4EC), // 연분홍
                    border = BorderStroke(2.5.dp, Color(0xFFF06292)),
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📖", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "일기장",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color(0xFF880E4F)
                        )
                    }
                }

                // --- 3. 커뮤니티 (싱그러운 민트-그린 테마) ---
                Surface(
                    onClick = { onNeighborNavigateClick() },
                    modifier = Modifier.weight(1f).height(100.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFFE0F2F1), // 연한 민트
                    border = BorderStroke(2.5.dp, Color(0xFF4DB6AC)),
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎈", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "커뮤니티",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color(0xFF00695C)
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
            onSettingNavigateClick = {},
            onFirstGameNavigateClick = {},
            onSecondGameNavigateClick = {},
            onThirdGameNavigateClick = {},
            mapUrl = "area/forest.jpg",
            patDataList = listOf(Pat(url = "pat/cat.json")),
            itemDataList = listOf(Item(url = "item/airplane.json")),
            dialogPatId = "0",
            dialogPatIdChange = {},
            userFlowDataList = flowOf(listOf(User(id = "money", value = "1000", value2 = "100000"))),
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