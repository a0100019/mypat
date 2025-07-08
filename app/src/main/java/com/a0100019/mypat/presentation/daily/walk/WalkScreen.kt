package com.a0100019.mypat.presentation.daily.walk

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WalkScreen(
    walkViewModel: WalkViewModel = hiltViewModel()

) {

    val walkState : WalkState = walkViewModel.collectAsState().value

    val context = LocalContext.current

    walkViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is WalkSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    WalkScreen(
        walkDataList = walkState.walkDataList,
        userDataList = walkState.userDataList,

        todayWalk = walkState.todayWalk,
        walkState = walkState.walkState,
        totalWalkCount = walkState.totalWalkCount,
        totalSuccessCount = walkState.totalSuccessCount,
        today = walkState.today,
        calendarMonth = walkState.calendarMonth,
        successRate = walkState.successRate,
        maxContinuous = walkState.maxContinuous,
        sensor =  walkState.sensor,

        onTodayWalkSubmitClick = walkViewModel::onTodayWalkSubmitClick,
        onCalendarMonthChangeClick = walkViewModel::onCalendarMonthChangeClick
    )
}

@Composable
fun WalkScreen(

    walkDataList: List<Walk> = emptyList(),
    userDataList: List<User> = emptyList(),

    todayWalk: Int = 1000,
    totalWalkCount: String = "0",
    walkState: String = "",
    totalSuccessCount: Int = 0,
    today: String = "2025-07-08",
    calendarMonth: String = "2025-07",
    successRate: Int = 0,
    maxContinuous: Int = 0,
    sensor: Boolean = false,

    onCalendarMonthChangeClick: (String)-> Unit = {},
    onTodayWalkSubmitClick: ()-> Unit = {}

) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center, // ✅ 내부 내용물 중앙 정렬
            modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            ) {
                if(sensor){
                    Text(
                        text = "측정 정지 ->"
                    )
                    Image(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "별 아이콘",
                        modifier = Modifier
                            .clickable {

                            }
                    )
                } else {
                    Text(
                        text = "걸음 수 측정이 정지되었습니다. 시작하려면 버튼을 눌러주세요 ->"
                    )
                    Image(
                        painter = painterResource(id = R.drawable.cancel),
                        contentDescription = "별 아이콘",
                        modifier = Modifier
                            .clickable {

                            }
                    )
                }

            }
            StepProgressCircle(
                steps = todayWalk,
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(todayWalk != 0){
                    Text(
                        text = "충전된 걸음 수",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = todayWalk.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "로딩중...",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            Text(
                text = "10000보를 모아 일일 미션을 완료하세요!",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        ShinyMissionCard()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            Text(
                text = "📊 걸음 수 통계",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.CenterHorizontally)
                ,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "총 걸음", tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "총 걸음 수 : ",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = totalWalkCount,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(Icons.Default.ThumbUp, contentDescription = "최고 기록", tint = Color(0xFFFFC107))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "성공 비율 : ",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${successRate}%",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, contentDescription = "만보 달성", tint = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "만보 달성 횟수 : ",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(text = "$totalSuccessCount")

                Spacer(modifier = Modifier.weight(1f))

                Icon(Icons.Default.Refresh, contentDescription = "연속 횟수", tint = Color(0xFF00BCD4))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "최대 연속 횟수 : ",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(text = "$maxContinuous")
            }
        }

        Column (
            modifier = Modifier
                .weight(0.4f)
                .padding(10.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                ,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "별 아이콘",
                    modifier = Modifier
                        .rotate(270f)
                        .clickable(
                            indication = null, // ← ripple 효과 제거
                            interactionSource = remember { MutableInteractionSource() } // ← 필수
                        ) {
                            onCalendarMonthChangeClick("left")
                        }
                )
                Text(
                    text = "오늘로",
                    modifier = Modifier
                        .clickable(
                            indication = null, // ← ripple 효과 제거
                            interactionSource = remember { MutableInteractionSource() } // ← 필수
                        ) {
                            onCalendarMonthChangeClick("today")
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "별 아이콘",
                    modifier = Modifier
                        .rotate(90f)
                        .clickable(
                            indication = null, // ← ripple 효과 제거
                            interactionSource = remember { MutableInteractionSource() } // ← 필수
                        ) {
                            onCalendarMonthChangeClick("right")
                        }
                )
            }

            Text(
                text = calendarMonth,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(top = 6.dp)
            )

            CalendarView(
                today = today,
                calendarMonth = calendarMonth,
                walkList = walkDataList.filter { it.success == "1" }
            )

        }

    }

}

@Composable
fun ShinyMissionCard() {
    // 애니메이션을 위한 각도
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing)
        ), label = ""
    )

    // 무지개색 그라디언트 (회전 애니메이션용)
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(Color.Yellow, Color.White, Color.Cyan),
        start = Offset.Zero,
        end = Offset(x = cos(Math.toRadians(angle.toDouble())).toFloat() * 300f, y = sin(Math.toRadians(angle.toDouble())).toFloat() * 300f)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(2.dp, Color(0xFFFFF176), shape = MaterialTheme.shapes.medium) // 연노랑 테두리
            .shadow(8.dp, shape = MaterialTheme.shapes.medium) // 그림자
            .background(shimmerBrush, shape = MaterialTheme.shapes.medium) // 반짝이는 배경
            .padding(16.dp)
    ) {
        Text(
            text = "10000보를 모아 일일 미션을 완료하세요!",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}


@Composable
fun CalendarView(
    today: String,
    calendarMonth: String, // 예: "2025-04"
    walkList: List<Walk>,
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val yearMonth = YearMonth.parse(calendarMonth, formatter)

    val year = yearMonth.year
    val month = yearMonth.monthValue

    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0

    // 걸은 날짜 리스트 (LocalDate 형태로 변환)
    val walkedDates = walkList.map { LocalDate.parse(it.date) }.toSet()

    val dates = mutableListOf<LocalDate?>()
    repeat(startDayOfWeek) { dates.add(null) }
    repeat(daysInMonth) { dates.add(firstDayOfMonth.plusDays(it.toLong())) }

    // 마지막 주도 7칸 맞추기 위해 빈 칸 추가
    val totalCells = ((dates.size + 6) / 7) * 7
    repeat(totalCells - dates.size) { dates.add(null) }

    Column {
        // 요일 헤더
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEachIndexed { index, day ->
                val textColor = when (index) {
                    0 -> Color(0xFFFF8A80) // 파스텔 빨강 (일)
                    6 -> Color(0xFF64B5F6) // 파스텔 파랑 (토)
                    else -> Color.Unspecified
                }

                Text(
                    text = day,
                    color = textColor,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        val todayDate = LocalDate.parse(today) // ← 오늘 날짜

        // 날짜 셀
        dates.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEachIndexed { index, date ->
                    val isWalked = date != null && date in walkedDates
                    val isToday = date != null && date == todayDate

                    val textColor = when (index) {
                        0 -> Color(0xFFFF8A80) // 일요일
                        6 -> Color(0xFF64B5F6) // 토요일
                        else -> Color.Unspecified
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.2f)
                            .padding(2.dp)
                            .background(
                                if (isWalked) Color(0xFFB2DFDB) else Color.Transparent
                            )
                        ,
                        contentAlignment = Alignment.Center
                    ) {
                        // 동그란 배경 레이어
                        if (isToday) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFE1BEE7), shape = CircleShape) // 연보라 배경
                            )
                        }

                        // 날짜 텍스트
                        Text(
                            text = date?.dayOfMonth?.toString() ?: "",
                            color = textColor
                        )
                    }
                }
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun WalkScreenPreview() {
    MypatTheme {
        WalkScreen(
            walkDataList = listOf(Walk(date = "2025-07-08", success = "1"), Walk(date = "2025-07-12", success = "1"))
        )
    }
}