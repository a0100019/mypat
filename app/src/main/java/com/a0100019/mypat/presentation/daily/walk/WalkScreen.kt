package com.a0100019.mypat.presentation.daily.walk

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

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
        walkWeeksDataList = walkState.walkWeeksDataList,

        todayWalk = walkState.todayWalk,
        mode = walkState.chartMode,
        totalWalkCount = walkState.totalWalkCount,
        maxWalkCount = walkState.maxWalkCount,
        goalCount = walkState.goalCount,

        changeChartMode = walkViewModel::changeChartMode,
    )
}

@Composable
fun WalkScreen(
    walkDataList: List<Walk>,
    walkWeeksDataList: List<Walk>,

    todayWalk: Int,
    mode: String,
    totalWalkCount: Int,
    maxWalkCount: Int,
    goalCount: Int,

    changeChartMode: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center, // ✅ 내부 내용물 중앙 정렬
            modifier = Modifier
                .weight(0.3f)
        ) {
            StepProgressCircle(
                steps = todayWalk,
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "오늘의 걸음 수", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "$todayWalk 걸음", fontSize = 32.sp, fontWeight = FontWeight.Bold)

            }
        }

        Row(
            modifier = Modifier
                .weight(0.1f)
                .padding(start = 16.dp, end = 16.dp)
        ) {


            repeat(walkWeeksDataList.size) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f) // 균등 배치
                        .aspectRatio(1f) // 정사각형 유지
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    StepProgressCircle(steps = walkWeeksDataList[walkWeeksDataList.size - 1 - index].count)
                    Text(walkWeeksDataList[walkWeeksDataList.size - 1 - index].date)
                }
            }

            repeat(7 - walkWeeksDataList.size) {
                Box(
                    modifier = Modifier
                        .weight(1f) // 균등 배치
                        .aspectRatio(1f) // 정사각형 유지
                        .padding(5.dp)
                ) {
                    StepProgressCircle(steps = 0)
                }
            }

        }


        Column(
            modifier = Modifier.weight(0.1f)
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "총 걸음 수 : $totalWalkCount",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = "최고 기록 : $maxWalkCount",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                Text(
                    text = "만보 달성 횟수 : $goalCount",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = "일일 만보 : ",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if(todayWalk > 10000) "오늘도 고생하셨어요!" else if(todayWalk > 5000) "조금만 더 힘내세요!" else "오늘도 힘내봅시다!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Column (
            modifier = Modifier
                .weight(0.4f)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Text(
                    text =
                    when(mode) {
                        "일" -> "일일 걸음 수"
                        "주" -> "주 평균 걸음 수"
                        else -> "월 평균 걸음 수"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f)) // 오른쪽 균형 맞추기

                Text(
                    text = "일",
                    modifier = Modifier
                        .clickable(
                            indication = null, // ← Ripple 효과 제거
                            interactionSource = remember { MutableInteractionSource() } // ← 필수
                            //클릭 효과 끄기
                        ) {
                            changeChartMode("일")
                        }
                        .padding(4.dp) // 내부 여백
                        .shadow(4.dp, RoundedCornerShape(8.dp)) // 그림자 효과
                        .background( if(mode=="일")Color(0xFFFFEB3B) else Color(0xFFE0F7FA), RoundedCornerShape(8.dp)) // 배경색 + 둥근 모서리
                        .padding(horizontal = 12.dp), // 텍스트 주변 padding
                    color = Color.Black, // 텍스트 색상
                )
                Text(
                    text = "주",
                    modifier = Modifier
                        .clickable(
                            indication = null, // ← Ripple 효과 제거
                            interactionSource = remember { MutableInteractionSource() } // ← 필수
                            //클릭 효과 끄기
                        ) {
                            changeChartMode("주")
                        }
                        .padding(4.dp) // 내부 여백
                        .shadow(4.dp, RoundedCornerShape(8.dp)) // 그림자 효과
                        .background( if(mode=="주")Color(0xFFFFEB3B) else Color(0xFFE0F7FA), RoundedCornerShape(8.dp)) // 배경색 + 둥근 모서리
                        .padding(horizontal = 12.dp), // 텍스트 주변 padding
                    color = Color.Black, // 텍스트 색상
                )
                Text(
                    text = "월",
                    modifier = Modifier
                        .clickable(
                            indication = null, // ← Ripple 효과 제거
                            interactionSource = remember { MutableInteractionSource() } // ← 필수
                            //클릭 효과 끄기
                        ) {
                            changeChartMode("월")
                        }
                        .padding(4.dp)
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(
                            if (mode == "월") Color(0xFFFFEB3B) else Color(0xFFE0F7FA),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp),
                    color = Color.Black,
                )

            }
            WalkLineChart(
                walkDataList = walkDataList,
                todayWalk = todayWalk,
                mode = mode
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun WalkScreenPreview() {
    MypatTheme {
        WalkScreen(
            todayWalk = 1234, // ✅ 테스트용 더미 걸음 수 (예: 1234 걸음)
            walkDataList = emptyList(),
            walkWeeksDataList = listOf(Walk(date = "11/22", count = 1000), Walk(date = "12/02", count = 5000), Walk(date = "12/22", count = 20000),),
            changeChartMode = {},
            mode = "일",
            maxWalkCount = 1000,
            totalWalkCount = 10000,
            goalCount = 100
        )
    }
}