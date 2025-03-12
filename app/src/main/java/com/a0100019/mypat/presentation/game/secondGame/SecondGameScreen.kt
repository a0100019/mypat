package com.a0100019.mypat.presentation.game.secondGame

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SecondGameScreen(
    secondGameViewModel: SecondGameViewModel = hiltViewModel()

) {

    val secondGameState : SecondGameState = secondGameViewModel.collectAsState().value

    val context = LocalContext.current

    secondGameViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SecondGameSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    SecondGameScreen(
        time = secondGameState.time,
        gameState = secondGameState.gameState,
        targetList = secondGameState.targetList,
        goalList = secondGameState.goalList,
        patData = secondGameState.patData,
        userData = secondGameState.userData,
        level = secondGameState.level,
        onItemSelected = secondGameViewModel::onItemSelected,
        onGameStartClick = secondGameViewModel::onGameStartClick,
        onNextLevelClick = secondGameViewModel::onNextLevelClick,
        onFinishClick = secondGameViewModel::onFinishClick,
        onGameReStartClick = secondGameViewModel::onGameReStartClick,
    )
}



@SuppressLint("DefaultLocale")
@Composable
fun SecondGameScreen(
    time : Double,
    gameState : String,
    patData : Pat,
    level : Int,

    userData : List<User>,
    targetList : List<Int>,
    goalList : List<Int>,

    onItemSelected : (Int) -> Unit,
    onGameStartClick : () -> Unit,
    onNextLevelClick : () -> Unit,
    onFinishClick : () -> Unit,
    onGameReStartClick: () -> Unit,
) {

    if (gameState == "성공" || gameState == "신기록") {
        SecondGameDialog(
            onClose = onGameReStartClick,
            userData = userData,
            patData = patData,
            situation = gameState,
            time = time
        )
    }

    if (gameState == "실패") {
        SecondGameOverDialog(
            onClose = onGameReStartClick,
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(String.format("%.2f", time))
        Text(level.toString())
        Box(
            modifier = Modifier
                .size(80.dp)
                .aspectRatio(1f) // 정사각형 유지
                .padding(4.dp)
                .background(
                    when (goalList[0]) {
                        1 -> Color.Red
                        2 -> Color.Magenta
                        3 -> Color.Green
                        4 -> Color.Blue
                        5 -> Color.Cyan
                        6 -> Color.DarkGray
                        else -> Color.LightGray
                    },
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if(goalList[0] == 6) {
                DialogPatImage(patData.url)
            }
        }

        Box(
            modifier = Modifier
                .size(60.dp)
                .aspectRatio(1f) // 정사각형 유지
                .padding(4.dp)
                .background(
                    when (goalList[1]) {
                        1 -> Color.Red
                        2 -> Color.Magenta
                        3 -> Color.Green
                        4 -> Color.Blue
                        5 -> Color.Cyan
                        6 -> Color.DarkGray
                        else -> Color.LightGray
                    },
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if(goalList[0] == 6) {
                DialogPatImage(patData.url)
            }
        }

        Column {
            targetList.chunked(5).forEachIndexed { rowIndex, rowItems -> // 행 인덱스
                Row {
                    rowItems.forEachIndexed { columnIndex, item -> // 열 인덱스
                        val actualIndex = rowIndex * 5 + columnIndex // 실제 인덱스 계산

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f) // 정사각형 유지
                                .padding(4.dp)
                                .background(
                                    when (item) {
                                        1 -> Color.Red
                                        2 -> Color.Magenta
                                        3 -> Color.Green
                                        4 -> Color.Blue
                                        5 -> Color.Cyan
                                        6 -> Color.DarkGray
                                        else -> Color.LightGray
                                    }, shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onItemSelected(actualIndex) // 클릭 시 인덱스 전달
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if(goalList[0] == 6) {
                                DialogPatImage(patData.url)
                            }
                        }
                    }
                }
            }
        }

        when (gameState) {
            "시작" -> {
                Button(
                    onClick = onGameStartClick
                ) {
                    Text("start")
                }
            }
            "진행" -> {
                Button(
                    onClick = onNextLevelClick
                ) {
                    Text("next level")
                }
            }
            "마지막" -> {
                Button(
                    onClick = onFinishClick
                ) {
                    Text("마지막")
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SecondGameScreenPreview() {
    MypatTheme {
        SecondGameScreen(
            time = 10.4,
            goalList = listOf(1, 2),
            targetList = listOf(1, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            onItemSelected = {},
            onGameStartClick = {},
            gameState = "진행",
            onNextLevelClick = {},
            patData = Pat(url = ""),
            onFinishClick = {},
            onGameReStartClick = {},
            userData = listOf(),
            level = 1
        )
    }
}