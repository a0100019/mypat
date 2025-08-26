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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
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
    walkViewModel: WalkViewModel = hiltViewModel(),
    popBackStack: () -> Unit = {},

) {

    val walkState: WalkState = walkViewModel.collectAsState().value

    val context = LocalContext.current

    walkViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is WalkSideEffect.Toast -> Toast.makeText(
                context,
                sideEffect.message,
                Toast.LENGTH_SHORT
            ).show()
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
        onCalendarMonthChangeClick = walkViewModel::onCalendarMonthChangeClick,
        popBackStack = popBackStack
//        onSensorChangeClick = walkViewModel::onSensorChangeClick
    )
}

@Composable
fun WalkScreen(

    walkDataList: List<Walk> = emptyList(),
    userDataList: List<User> = emptyList(),

    todayWalk: Int = 1000,
    totalWalkCount: String = "0",
    walkState: String = "완료",
    totalSuccessCount: Int = 0,
    today: String = "2025-07-15",
    calendarMonth: String = "2025-07",
    successRate: Int = 0,
    maxContinuous: Int = 0,
    sensor: Boolean = false,

    onCalendarMonthChangeClick: (String)-> Unit = {},
    onTodayWalkSubmitClick: ()-> Unit = {},
    onSensorChangeClick: () -> Unit = {},
    popBackStack: () -> Unit = {},

) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Box(
                contentAlignment = Alignment.Center, // ✅ 내부 내용물 중앙 정렬
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {

                StepProgressCircle(
                    steps = todayWalk,
                    modifier = Modifier
                        .size(200.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (todayWalk != 0) {
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "핸드폰을 흔들어주세요",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                }

                // 오른쪽 버튼
                MainButton(
                    text = "닫기",
                    onClick = popBackStack,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            if (walkState == "완료") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            MaterialTheme.colorScheme.scrim,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "오늘도 수고하셨어요!",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                if (todayWalk <= 10000) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                MaterialTheme.colorScheme.scrim,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "10000보를 모아 일일 미션을 완료하세요!",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    ShinyMissionCard(
                        onClick = onTodayWalkSubmitClick
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.scrim,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "📊 걸음 수 통계",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally),
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

                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = "최고 기록",
                        tint = Color(0xFFFFC107)
                    )
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
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "만보 달성",
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "만보 달성 횟수 : ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "$totalSuccessCount")

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "연속 횟수",
                        tint = Color(0xFF00BCD4)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "최대 연속 횟수 : ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "$maxContinuous")
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
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
                        text = "오늘로 이동",
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

}



@Preview(showBackground = true)
@Composable
fun WalkScreenPreview() {
    MypatTheme {
        WalkScreen(
            walkDataList = listOf(Walk(date = "2025-07-15", success = "1"), Walk(date = "2025-07-08", success = "1"), Walk(date = "2025-07-12", success = "1"))
        )
    }
}