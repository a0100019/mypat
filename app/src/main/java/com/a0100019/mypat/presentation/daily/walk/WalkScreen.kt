package com.a0100019.mypat.presentation.daily.walk

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.loading.LoadingSideEffect
import com.a0100019.mypat.presentation.loading.LoadingState
import com.a0100019.mypat.presentation.loading.LoadingViewModel
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
        todayWalk = walkState.todayWalk,

        startForegroundService = walkViewModel::startForegroundService,
        stopForegroundService = walkViewModel::stopForegroundService
    )
}



@Composable
fun WalkScreen(
    todayWalk : Int,
    startForegroundService : () -> Unit,
    stopForegroundService : () -> Unit
) {
    Column {
        Text(todayWalk.toString())
        Button(
            onClick = startForegroundService
        ) {
            Text("측정 시작")
        }
        Button(
            onClick = stopForegroundService
        ) {
            Text("측정 종료")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalkScreenPreview() {
    MypatTheme {
        WalkScreen(
            todayWalk = 100,
            startForegroundService = {},
            stopForegroundService = {},

        )
    }
}