package com.a0100019.mypat.presentation.daily.walk

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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

    )
}

@Composable
fun WalkScreen(
    todayWalk: Int,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center, // ✅ 내부 내용물 중앙 정렬
            modifier = Modifier
        ) {
            StepProgressCircle(todayWalk)
            Column(
            ) {
                Text(text = "오늘의 걸음 수", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "$todayWalk 걸음", fontSize = 32.sp, fontWeight = FontWeight.Bold)

            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun WalkScreenPreview() {
    MypatTheme {
        WalkScreen(
            todayWalk = 1234 // ✅ 테스트용 더미 걸음 수 (예: 1234 걸음)

        )
    }
}