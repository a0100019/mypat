package com.a0100019.mypat.presentation.daily.diary

import android.Manifest
import android.app.Activity
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.diary.Diary
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
                    Toast.makeText(context, "매일 $time 에 알기 알림이 설정됐어요 ⏰", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(context, "매일 $time 에 알람이 설정됐어요 ⏰", Toast.LENGTH_SHORT).show()
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

        onDiaryClick = diaryViewModel::onDiaryClick,
        onCloseClick = diaryViewModel::onCloseClick,
        onDiaryChangeClick = diaryViewModel::onDiaryChangeClick,
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
        onCancelAlarmClick = diaryViewModel::onCancelAlarmClick
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
    onCancelAlarmClick: () -> Unit = {}
) {

    AppBgmManager.pause()

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

        BackGroundImage()

        // Fullscreen container
        Column(
            modifier = Modifier
                .fillMaxSize(),

            ) {
            // Text in the center

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 20.dp, top = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                // 오른쪽 버튼
                MainButton(
                    text = "마을",
                    onClick = onNavigateToMainScreen,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

//                Text(
//                    text = "일기장",
//                    style = MaterialTheme.typography.displayMedium, // Large font size
//                    modifier = Modifier
//                )

                MainButton(
                    text = "공유소",
                    onClick = { onDialogStateChange("알림") },
                    modifier = Modifier.align(Alignment.Center)
                )

                // 오른쪽 버튼
                MainButton(
                    onClick = { onDialogStateChange("exit") },
                    text = "종료",
                    backgroundColor = MaterialTheme.colorScheme.tertiary,
                    borderColor = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.align(Alignment.CenterEnd)
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
                        .size(30.dp)
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
                                .padding(horizontal = 8.dp, vertical = 12.dp)
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
                                                text = "어떤 하루를 보냈나요?",
                                                fontFamily = safeFont,
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    letterSpacing = (-0.5).sp
                                                ),
                                                color = textColor,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }

//                                        // New 태그 디자인 업그레이드
//                                        Box(
//                                            modifier = Modifier
//                                                .background(accentColor, RoundedCornerShape(12.dp))
//                                                .padding(horizontal = 12.dp, vertical = 6.dp)
//                                        ) {
//                                            Text(
//                                                text = "WRITE",
//                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
//                                                color = Color.White
//                                            )
//                                        }
                                    }

//                                    // ✨ 고급스러운 Shimmer 레이어
//                                    Box(
//                                        modifier = Modifier
//                                            .matchParentSize()
//                                            .background(
//                                                brush = Brush.linearGradient(
//                                                    0.0f to Color.Transparent,
//                                                    0.5f to Color.White.copy(alpha = 0.5f),
//                                                    1.0f to Color.Transparent,
//                                                    start = Offset(shimmerX * 1000f, 0f),
//                                                    end = Offset((shimmerX + 0.3f) * 1000f, 500f)
//                                                )
//                                            )
//                                    )
                                }
                            }
                        }

                    } else {

                        // 1. 감정별 메인 색상 정의
                        val emotionColor = when (diaryData.emotion) {
                            "emotion/smile.png" -> Color(0xFFFFD54F)    // 노랑
                            "emotion/love.png" -> Color(0xFFF06292)     // 분홍
                            "emotion/exciting.png" -> Color(0xFFFF8A65) // 주황
                            "emotion/cry.png" -> Color(0xFF64B5F6)      // 파랑
                            "emotion/sad.png" -> Color(0xFF9575CD)      // 보라
                            "emotion/angry.png" -> Color(0xFFE57373)    // 빨강
                            "emotion/thinking.png" -> Color(0xFF90A4AE) // 회색
                            "emotion/normal.png" -> Color(0xFF81C784)   // 초록
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        val cardColor = emotionColor.copy(alpha = 0.7f)
                        val contentColor = Color.Black.copy(alpha = 0.8f)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onDiaryClick(diaryData) }
                                )
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // 1. 날짜랑 요일 Row로 같은 줄에 배치
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically // 날짜와 요일 아래쪽 정렬
                                    ) {
                                        Text(
                                            text = diaryData.date,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = contentColor
                                        )

                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        val date = LocalDate.parse(diaryData.date, formatter)

                                        Text(
                                            text = " ${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)}요일",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = contentColor.copy(alpha = 0.6f),
                                            modifier = Modifier.padding(start = 4.dp, bottom = 1.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    // 2. 감정 아이콘 (테두리/그림자 없이 깔끔하게 화이트 원형만 유지)
                                    Box(contentAlignment = Alignment.Center) {
                                        JustImage(
                                            filePath = diaryData.emotion,
                                            modifier = Modifier.size(25.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // 3. 본문 (따옴표 제거)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = diaryData.contents,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            lineHeight = 24.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = contentColor,
//                                        maxLines = 3,
                                        // overflow 설정을 따로 하지 않거나 Clip으로 설정하면 ...이 생기지 않습니다.
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                        }

                    }
                }

                // 2. 맨 밑에 알람 켜기 버튼 추가
                item {
                    val gradient = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFC1CC), // 파스텔 핑크
                            Color(0xFFB5EAEA)  // 파스텔 민트
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0x55B5EAEA),
                                spotColor = Color(0x55FFC1CC)
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .background(gradient)
                            .clickable {
                                // 🔔 알림 설정 클릭 처리
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "매일 정해진 시간에 일기 알림을 받아보아요",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
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