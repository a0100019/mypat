package com.a0100019.mypat.presentation.daily.walk

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

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
        userDataList = walkState.userDataList,
        today = walkState.today,
        calendarMonth = walkState.calendarMonth,
        saveSteps = walkState.saveSteps,
        stepsRaw = walkState.stepsRaw,
        situation = walkState.situation,
        baseDate = walkState.baseDate,

        onTodayWalkSubmitClick = walkViewModel::onTodayWalkSubmitClick,
        onCalendarMonthChangeClick = walkViewModel::onCalendarMonthChangeClick,
        popBackStack = popBackStack,
        onSituationChangeClick = walkViewModel::onSituationChangeClick
//        onSensorChangeClick = walkViewModel::onSensorChangeClick
    )
}

@Composable
fun WalkScreen(

    userDataList: List<User> = emptyList(),

    today: String = "2025-07-15",
    calendarMonth: String = "2025-07",
    saveSteps: Int = 0,
    stepsRaw: String = "2001-01-01.1",
    situation: String = "record",
    baseDate: String = "2025-07-15",

    onCalendarMonthChangeClick: (String)-> Unit = {},
    onTodayWalkSubmitClick: ()-> Unit = {},
    popBackStack: () -> Unit = {},
    onSituationChangeClick: () -> Unit = {},

) {

    val context = LocalContext.current
    val intent = Intent(context, StepForegroundService::class.java)
    context.startForegroundService(intent)

    val items = stepsRaw.split("/").filter { it.isNotBlank() }

    val walkMap = items
        .mapNotNull {
            val parts = it.split(".")
            if (parts.size == 2) parts[0] to parts[1].toInt() else null
        }
        .toMap()

    val totalSteps = walkMap.values.sum()

    // 마지막 기록 가져오기
    val last = items.lastOrNull()

    // 기본값 0
    var todaySteps = 0

    if (last != null) {
        val parts = last.split(".")
        if (parts.size == 2) {

            val date = parts[0]
            val steps = parts[1].toInt()

            // 🔥 last가 오늘 날짜일 때만 steps 적용
            if (date == today) {
                todaySteps = steps
            }
        }
    }

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
                    steps = todaySteps,
                    modifier = Modifier
                        .size(200.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "오늘 걸음 수",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = todaySteps.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
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

//            if (walkState == "완료") {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                        .background(
//                            MaterialTheme.colorScheme.scrim,
//                            shape = RoundedCornerShape(16.dp)
//                        )
//                        .border(
//                            width = 2.dp,
//                            color = MaterialTheme.colorScheme.primaryContainer,
//                            shape = RoundedCornerShape(16.dp)
//                        )
//                        .padding(16.dp)
//                ) {
//                    Text(
//                        text = "오늘도 수고하셨어요!",
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        textAlign = TextAlign.Center
//                    )
//                }
//            } else {
//                if (todayWalk <= 10000) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp)
//                            .background(
//                                MaterialTheme.colorScheme.scrim,
//                                shape = RoundedCornerShape(16.dp)
//                            )
//                            .border(
//                                width = 2.dp,
//                                color = MaterialTheme.colorScheme.primaryContainer,
//                                shape = RoundedCornerShape(16.dp)
//                            )
//                            .padding(16.dp)
//                    ) {
//                        Text(
//                            text = "10000보를 모아 일일 미션을 완료하세요!",
//                            style = MaterialTheme.typography.bodyLarge,
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            textAlign = TextAlign.Center
//                        )
//                    }
//                } else {
//                    ShinyMissionCard(
//                        onClick = onTodayWalkSubmitClick
//                    )
//                }
//            }

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.55f)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                MainButton(
                    text = when(situation) {
                        "record" -> "월간 기록 보기"
                        "month" -> "주간 기록 보기"
                        else -> "업적 보기"
                    },
                    onClick = onSituationChangeClick
                )

                Spacer(modifier = Modifier.size(8.dp))

                if(situation == "record"){

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
                                text = totalSteps.toString(),
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                Icons.Default.ThumbUp,
                                contentDescription = "최고 기록",
                                tint = Color(0xFFFFC107)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                        }

                    }
                } else {
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
                }

                if(situation == "month"){
                    Text(
                        text = calendarMonth,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 6.dp)
                    )

                    WalkCalendarView(
                        today = today,
                        calendarMonth = calendarMonth,
                        stepsRaw = stepsRaw
                    )
                } else if(situation == "week"){
                    Text(
                        text = "1주전",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 6.dp)
                    )

                    WalkWeekView(
                        today = today,
                        baseDate = baseDate,
                        stepsRaw = stepsRaw
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun WalkScreenPreview() {
    MypatTheme {
        WalkScreen(
            stepsRaw = "2025-07-01.10000/2025-07-03.2000/2025-07-15.10000"
        )
    }
}