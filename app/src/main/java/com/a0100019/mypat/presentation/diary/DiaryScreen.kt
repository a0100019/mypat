package com.a0100019.mypat.presentation.diary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.data.room.photo.Photo
import com.a0100019.mypat.domain.AppBgmManager
import com.a0100019.mypat.presentation.main.MainSideEffect
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.component.SparkleText
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DiaryScreen(
    diaryViewModel: DiaryViewModel = hiltViewModel(),

    onDiaryClick: () -> Unit,
    popBackStack: () -> Unit = {},
    onNavigateToMainScreen: () -> Unit,
    onNavigateToSettingScreen: () -> Unit = {}
) {

    val diaryState: DiaryState = diaryViewModel.collectAsState().value
    val context = LocalContext.current

    // 🔹 권한 요청 후 재사용할 시간
    var pendingTime by remember { mutableStateOf<String?>(null) }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            // 사용자가 팝업에서 '허용'을 눌렀는지 체크
            if (granted) {
                pendingTime?.let { time ->
                    scheduleDiaryAlarm(context, time)
                    Toast.makeText(context, "매일 $time 에 알림이 설정됐어요", Toast.LENGTH_SHORT).show()
                    diaryViewModel.onCloseClick()
                }
            } else {
                // 사용자가 '거부'를 눌렀을 때
                Toast.makeText(context, "알림 권한이 거절되었습니다.", Toast.LENGTH_SHORT).show()
            }
            // 처리가 끝났으므로 변수 비우기
            pendingTime = null
        }

    // 🔹 SideEffect 수신
    diaryViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {

            is DiarySideEffect.Toast ->
                Toast.makeText(
                    context,
                    sideEffect.message,
                    Toast.LENGTH_SHORT
                ).show()

            DiarySideEffect.NavigateToDiaryWriteScreen ->
                onDiaryClick()

            DiarySideEffect.ExitApp ->
                (context as? Activity)?.finish()

            is DiarySideEffect.CheckNotificationPermission -> {
                val time = sideEffect.timeString

                // 1. 이미 권한이 있는지 확인
                val isAlreadyGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }

                if (isAlreadyGranted) {
                    // 이미 권한이 있으면 팝업 없이 바로 설정
                    scheduleDiaryAlarm(context, time)
                    Toast.makeText(context, "매일 $time 에 알림이 설정됐어요", Toast.LENGTH_SHORT).show()
                    diaryViewModel.onCloseClick()
                } else {
                    // 권한이 없으면 팝업을 띄우기 위해 시간을 저장하고 런처 실행
                    pendingTime = time
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

        }
    }

    // 🔹 실제 UI 화면 (이름 충돌 없게 분리되어 있다고 가정)
    DiaryScreen(
        diaryDataList = diaryState.diaryFilterDataList,
        clickDiaryData = diaryState.clickDiaryData,
        dialogState = diaryState.dialogState,
        searchText = diaryState.searchText,
        emotionFilter = diaryState.emotionFilter,
        today = diaryState.today,
        calendarMonth = diaryState.calendarMonth,
        photoDataList = diaryState.photoDataList,
        clickPhoto = diaryState.clickPhoto,

        onDiaryClick = diaryViewModel::onDiaryClick,
        onCloseClick = diaryViewModel::onCloseClick,
        onDiaryChangeClick = diaryViewModel::onDiaryChangeClick,
        clickPhotoChange = diaryViewModel::clickPhotoChange,
        onSearchClick = diaryViewModel::onSearchClick,
        onSearchTextChange = diaryViewModel::onSearchTextChange,
        onDialogStateChange = diaryViewModel::onDialogStateChange,
        onEmotionFilterClick = diaryViewModel::onEmotionFilterClick,
        onSearchClearClick = diaryViewModel::onSearchClearClick,
        onCalendarMonthChangeClick = diaryViewModel::onCalendarMonthChangeClick,
        onDiaryDateClick = diaryViewModel::onDiaryDateClick,
        onCalendarDiaryCloseClick = diaryViewModel::onCalendarDiaryCloseClick,
        onNavigateToMainScreen = onNavigateToMainScreen,
        popBackStack = popBackStack,
        onExitClick = diaryViewModel::onExitClick,
        onDiaryAlarmChangeClick = diaryViewModel::onDiaryAlarmChangeClick,
        onCancelAlarmClick = diaryViewModel::onCancelAlarmClick,
        onNavigateToSettingScreen = onNavigateToSettingScreen
    )
}

@Composable
fun DiaryScreen(
    diaryDataList: List<Diary>,

    clickDiaryData: Diary?,
    dialogState: String,
    searchText: String,
    emotionFilter: String,
    today: String = "2025-07-15",
    calendarMonth: String = "2025-07",
    photoDataList: List<Photo> = emptyList(),
    clickPhoto: String = "",

    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onDiaryClick: (Diary) -> Unit,
    onCloseClick: () -> Unit,
    onExitClick: () -> Unit = {},
    onDiaryChangeClick: () -> Unit,
    onDialogStateChange: (String) -> Unit = {},
    onEmotionFilterClick: (String) -> Unit,
    onSearchClearClick: () -> Unit,
    popBackStack: () -> Unit = {},
    onCalendarMonthChangeClick: (String)-> Unit = {},
    onCalendarDiaryCloseClick: () -> Unit = {},
    onDiaryDateClick: (String) -> Unit = {},
    onNavigateToMainScreen: () -> Unit = {},
    onDiaryAlarmChangeClick: (String) -> Unit = {},
    onCancelAlarmClick: () -> Unit = {},
    onNavigateToSettingScreen: () -> Unit = {},
    clickPhotoChange: (String) -> Unit = {}
) {

    AppBgmManager.pause()

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("diary_prefs", Context.MODE_PRIVATE) }

    // 상태로 관리하여 리컴포지션 시에도 유지되도록 함
    var alarmState by remember { mutableStateOf(prefs.getString("alarm", "0")) }

    if (alarmState == "1") {
        DiaryAlarmDialog(
            onClose = onCloseClick,
            onConfirmClick = {
                onDiaryAlarmChangeClick(it)
                alarmState = "2"
                             },
            onCancelClick = onCancelAlarmClick
        )

        // 화면에 진입했을 때 딱 한 번만 실행됨
        LaunchedEffect(Unit) {
            prefs.edit().putString("alarm", "2").apply()
            // 필요하다면 로컬 상태도 업데이트하여 일관성 유지
            // alarmState = "2"
        }
    }
    if(clickDiaryData != null && dialogState == "") {
        DiaryReadDialog(
            onClose = onCloseClick,
            diaryData = clickDiaryData,
            onDiaryChangeClick = onDiaryChangeClick
        )
    } else if(clickDiaryData != null && dialogState == "달력") {
        DiaryReadDialog(
            onClose = onCalendarDiaryCloseClick,
            diaryData = clickDiaryData,
            onDiaryChangeClick = onDiaryChangeClick
        )
    }

    if(clickPhoto != "") {
        DiaryPhotoDialog(
            onClose = { clickPhotoChange("") },
            clickPhoto = clickPhoto
        )
    }

    when(dialogState) {
        "검색" -> DiarySearchDialog(
            onClose = onSearchClearClick,
            onSearchTextChange = onSearchTextChange,
            searchString = searchText,
            onConfirmClick = onSearchClick,
        )
        "감정" -> DiaryEmotionDialog(
            onClose = onCloseClick,
            onEmotionClick = onEmotionFilterClick,
            removeEmotion = true
        )
        "달력" -> DiaryCalendarDialog(
            onClose = onCloseClick,
            onCalendarMonthChangeClick = onCalendarMonthChangeClick,
            today = today,
            calendarMonth = calendarMonth,
            diaryDataList = diaryDataList,
            onDiaryDateClick = onDiaryDateClick
        )
        "알림" -> DiaryAlarmDialog(
            onClose = onCloseClick,
            onConfirmClick = onDiaryAlarmChangeClick,
            onCancelClick = onCancelAlarmClick
        )
        "exit" -> SimpleAlertDialog(
            onConfirmClick = onExitClick,
            onDismissClick = onCloseClick,
            text = "하루마을을 종료하시겠습니까?",
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

//        BackGroundImage()

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE0C3FC), // 연한 보라
                                Color(0xFF8EC5FC)  // 연한 하늘
                            )
                        )
                    )
            ) {
                // 컨텐츠 공간
            }
        }

        // Fullscreen container
        Column(
            modifier = Modifier
                .fillMaxSize(),

            ) {
            // Text in the center

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 24.dp, top = 10.dp),
                contentAlignment = Alignment.Center
            ) {

                JustImage(
                    filePath = "etc/cog.png",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onNavigateToSettingScreen()
                        }
                        .align(Alignment.CenterStart)
                    ,
                )

                Surface(
                    onClick = onNavigateToMainScreen,
                    shape = RoundedCornerShape(30.dp),
                    // 1. 단색 대신 아주 미세한 그라데이션 효과를 위해 컬러 살짝 조정
                    color = Color(0xFFFFF9C4),
                    border = BorderStroke(2.5.dp, Color(0xFFFFD54F)), // 테두리를 살짝 더 두껍게 해서 선명하게
                    modifier = Modifier
                        .height(50.dp) // 조금 더 도톰하게 만들어 클릭감을 높임
                        .padding(horizontal = 8.dp),
                    shadowElevation = 8.dp, // 입체감을 조금 더 강조
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                // 2. 버튼 내부에 부드러운 빛 반사 효과 (상단이 조금 더 밝게)
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFFEF0), // 상단부 밝은 노랑
                                        Color(0xFFFFF9C4)  // 하단부 기본 노랑
                                    )
                                )
                            )
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // 3. 귀여운 아이콘 추가 (마을로 떠나는 느낌)
                        JustImage(
                            filePath = "etc/home.png",
                            modifier = Modifier
                                .size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "마을로 이동하기",
                            style = MaterialTheme.typography.titleSmall.copy( // 크기를 살짝 키움
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.7).sp
                            ),
                            color = Color(0xFF5D4037)
                        )

                    }
                }

                // [오른쪽] 종료 버튼 (🚪 나가기 아이콘 스타일)
                JustImage(
                    filePath = "etc/switch.png",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onDialogStateChange("exit")
                        }
                        .align(Alignment.CenterEnd)
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 20.dp, start = 20.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

//                MainButton(
//                    onClick = {
//                        onDialogStateChange("달력")
//                    },
//                    text = " 달력 보기 "
//                )

                JustImage(
                    filePath = emotionFilter,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable(
//                            indication = null, // 🔕 클릭 효과 제거
//                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onDialogStateChange("감정")
                        }
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.Notifications, // 종 모양 아이콘
                    contentDescription = "알람 아이콘",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable { onDialogStateChange("알림") }
                    ,
                    tint = Color.Black
                )

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "회전된 이미지",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable(
//                            indication = null, // 🔕 클릭 효과 제거
//                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onDialogStateChange("달력")
                        }
                )

                Spacer(modifier = Modifier.width(8.dp))

//                MainButton(
//                    onClick = {
//                        onDialogStateChange("검색")
//                    },
//                    text = " 검색 "
//                )

                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "회전된 이미지",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
//                            indication = null, // 🔕 클릭 효과 제거
//                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onDialogStateChange("검색")
                        }
                )
            }

            // 1. 전체 사진 리스트를 날짜별로 묶어버립니다 (일기 리스트 밖에서 한 번만 수행)
            val photosByDate = remember(photoDataList) {
                photoDataList.groupBy { it.date }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp) // 카드 사이 간격 추가
            ) {
                itemsIndexed(diaryDataList) { index, diaryData ->

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    val monthChange = index > 0 && diaryData.date.substring(
                        5,
                        7
                    ) != diaryDataList[index - 1].date.substring(5, 7)

                    if (monthChange) {
                        Text(
                            text = diaryData.date.substring(0, 7).split("-").let {
                                "${it[0]}년 ${it[1]}월"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp),
                            style = MaterialTheme.typography.titleLarge.copy(
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    if (diaryData.state == "대기") {
                        // 1. 애니메이션 설정 (기존 로직 유지)
                        val infiniteTransition = rememberInfiniteTransition(label = "new_diary_anim")
                        val floatingOffset by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = -10f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1800, easing = EaseInOutSine),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "floating"
                        )

                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f)

                        val shimmerX by infiniteTransition.animateFloat(
                            initialValue = -0.5f,
                            targetValue = 1.5f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(3000, easing = LinearEasing)
                            ),
                            label = "shimmer"
                        )

                        // 2. 색상 정의 (세련된 파스텔 테마)
                        val baseColor = Color(0xFFF1F8E9) // 아주 연한 민트 크림
                        val accentColor = Color(0xFF81C784) // 부드러운 초록
                        val textColor = Color(0xFF2E7D32) // 깊은 초록 (글씨용)

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val date = LocalDate.parse(diaryData.date, formatter)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    translationY = floatingOffset.dp.toPx()
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onDiaryClick(diaryData) }
                                )
                        ) {
                            // [하단 그림자 층] - 실제 물리적 버튼처럼 보이게 함
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .offset(y = 8.dp),
                                shape = RoundedCornerShape(28.dp),
                                color = accentColor.copy(alpha = 0.2f)
                            ) {}

                            // [메인 카드 층]
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                shape = RoundedCornerShape(28.dp),
                                color = baseColor,
                                border = BorderStroke(2.dp, accentColor.copy(alpha = 0.4f))
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {

                                    // 왼쪽 상단 작은 포인트 (날짜 표식)
                                    Box(
                                        modifier = Modifier
                                            .size(width = 60.dp, height = 4.dp)
                                            .align(Alignment.TopStart)
                                            .padding(start = 24.dp, top = 12.dp)
                                            .background(accentColor.copy(alpha = 0.3f), CircleShape)
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 24.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = diaryData.date,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = textColor.copy(alpha = 0.6f)
                                                )
                                                Text(
                                                    text = " ${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)}요일",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = textColor.copy(alpha = 0.4f),
                                                    modifier = Modifier.padding(start = 4.dp)
                                                )
                                            }

                                            val isPreview = LocalInspectionMode.current // 프리뷰 감지
                                            // 폰트 설정
                                            val customFont = FontFamily(Font(R.font.fish))
                                            val safeFont = if (isPreview) FontFamily.SansSerif else customFont

                                            Text(
                                                text = "오늘 어떤 하루를 보냈나요?",
                                                fontFamily = safeFont,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    letterSpacing = (-0.5).sp
                                                ),
                                                color = textColor,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }

                                    }

                                }
                            }
                        }

                    } else {

                        val emotionColor = when (diaryData.emotion) {
                            "emotion/smile.png" -> Color(0xFFFFD54F)    // 노랑 (기존)
                            "emotion/love.png" -> Color(0xFFF48FB1)     // 화사한 핑크 (더 밝고 부드럽게)
                            "emotion/exciting.png" -> Color(0xFFBA68C8) // 귤색/주황 (빨강기를 빼고 노랑 주황으로)
                            "emotion/cry.png" -> Color(0xFF4FC3F7)      // 밝은 하늘색 (시원하게)
                            "emotion/sad.png" -> Color(0xFFA1887F)      // 보라 (라벤더 느낌)
                            "emotion/angry.png" -> Color(0xFFEF5350)    // 강렬한 레드 (분홍/주황과 확실히 차이나는 빨강)
                            "emotion/thinking.png" -> Color(0xFFAFBBC1) // 차분한 블루그레이 (그냥 회색보다 세련됨)
                            "emotion/normal.png" -> Color(0xFF9CCC65)   // 싱그러운 연두색
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onDiaryClick(diaryData) }
                                ),
                            shape = RoundedCornerShape(18.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, emotionColor.copy(alpha = 0.2f)) // 전체 카드 테두리
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {

                                // [상단 영역] 감정 색상이 은은하게 깔린 헤더
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(emotionColor.copy(alpha = 0.15f)) // 상단만 감정색 채우기
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 날짜와 요일
                                    Column {
                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        val date = LocalDate.parse(diaryData.date, formatter)

                                        Text(
                                            text = diaryData.date,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF333333)
                                        )
                                        Text(
                                            text = "${date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.Gray
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    // 감정 아이콘을 더 돋보이게 하는 화이트 칩
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White,
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            JustImage(
                                                filePath = diaryData.emotion,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }

                                // [구분선] 상단과 본문을 나누는 얇은 선
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(emotionColor.copy(alpha = 0.2f))
                                )

                                // [본문 영역]
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = diaryData.contents,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontSize = 16.sp,
                                            lineHeight = 28.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                val filteredPhotos = photosByDate[diaryData.date] ?: emptyList()

                                // 2. 걸러낸 사진이 있을 때만 영역을 렌더링합니다.
                                if (filteredPhotos.isNotEmpty()) {
                                    Column( // LazyRow를 감싸는 여백 처리를 위해 Column으로 변경 제안
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    ) {
                                        LazyRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp) // 카드 좌우 여백과 맞춤
                                        ) {
                                            // 전체 리스트가 아닌 필터링된 리스트를 사용하세요!
                                            items(filteredPhotos.reversed()) { photo ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(84.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .border(
                                                            1.dp,
                                                            Color.LightGray.copy(alpha = 0.5f),
                                                            RoundedCornerShape(12.dp)
                                                        )
                                                ) {
                                                    AsyncImage(
                                                        model = photo.localPath,
                                                        contentDescription = "일기 사진",
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clickable {
                                                                // 아까 만든 확대 함수 호출
                                                                clickPhotoChange(photo.localPath)
                                                            },
                                                        contentScale = ContentScale.Crop
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

//                // 2. 맨 밑에 알람 켜기 버튼 추가
//                item {
//                    val gradient = Brush.horizontalGradient(
//                        colors = listOf(
//                            Color(0xFFFFC1CC), // 파스텔 핑크
//                            Color(0xFFB5EAEA)  // 파스텔 민트
//                        )
//                    )
//
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 24.dp, vertical = 20.dp)
//                            .shadow(
//                                elevation = 8.dp,
//                                shape = RoundedCornerShape(24.dp),
//                                ambientColor = Color(0x55B5EAEA),
//                                spotColor = Color(0x55FFC1CC)
//                            )
//                            .clip(RoundedCornerShape(24.dp))
//                            .background(gradient)
//                            .clickable {
//                                // 🔔 알림 설정 클릭 처리
//                            }
//                            .padding(vertical = 16.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Notifications,
//                                contentDescription = null,
//                                tint = Color.White,
//                                modifier = Modifier.size(22.dp)
//                            )
//
//                            Spacer(modifier = Modifier.width(8.dp))
//
//                            Text(
//                                text = "매일 정해진 시간에 일기 알림을 받아보아요",
//                                style = MaterialTheme.typography.titleMedium,
//                                color = Color.White
//                            )
//                        }
//                    }
//                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryScreenPreview() {
    MypatTheme {
        DiaryScreen(

            clickDiaryData = null,
            dialogState = "",
            searchText = "",

            onDiaryClick = {},
            onCloseClick = {},
            onDiaryChangeClick = {},
            onSearchClick = {},
            onSearchTextChange = {},
            onDialogStateChange = {},
            onEmotionFilterClick = {},
            onSearchClearClick = {},
            emotionFilter = "etc/snowball.png",

            diaryDataList = listOf(
                Diary(date = "2025-02-07", emotion = "", contents = ""),
                Diary(date = "2025-02-06", emotion = "emotion/smile.png", contents = "안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕", state = "완료"),
                Diary(date = "2025-02-07", emotion = "", contents = ""),
                Diary(date = "2025-02-06", emotion = "happy", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-07", emotion = "", contents = ""),
                Diary(date = "2025-01-05", emotion = "happy", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-06", emotion = "", contents = ""),
                Diary(date = "2025-02-07", emotion = "happy", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-08", emotion = "", contents = "")
            ),

        )
    }
}