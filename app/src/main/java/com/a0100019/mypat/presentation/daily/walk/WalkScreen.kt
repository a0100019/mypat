@file:Suppress("UNUSED_EXPRESSION")

package com.a0100019.mypat.presentation.daily.walk

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.walk.Walk
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import kotlinx.coroutines.flow.MutableStateFlow
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
        todayWalk = walkState.todayWalk,
        walkDataList = walkState.walkDataList,
        mode = walkState.chartMode,

        changeWalkMode = walkViewModel::changeChartMode,
    )
}

@Composable
fun WalkScreen(
    walkDataList: List<Walk>,

    todayWalk: Int,
    mode: String,

    changeWalkMode: (String) -> Unit,
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
            ) {
                Text(text = "오늘의 걸음 수", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "$todayWalk 걸음", fontSize = 32.sp, fontWeight = FontWeight.Bold)

            }
        }

        Row(
            modifier = Modifier.weight(0.1f)
        ) {
            StepProgressCircle(
                steps = todayWalk,
                )
        }

        Column(
            modifier = Modifier.weight(0.1f)
        ) {
            Row {
                Text(text = "총 걸음 수 : 1999999")
            }
            Row {
                Text(text = "최고 기록 : 34000")
                Text(text = "만보 달성 횟수 : 5")
            }
        }

        Column (
            modifier = Modifier
                .weight(0.4f)
                .padding(10.dp)
        ) {
            Row {
                Text(
                    text = " 일 ",
                    modifier = Modifier.clickable {

                    },
                )
                Text(
                        text = " 주 ",
                modifier = Modifier.clickable {
                    changeWalkMode("주")
                },
                )
                Text(" 월 ")
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
            changeWalkMode = {},
            mode = "일"
        )
    }
}