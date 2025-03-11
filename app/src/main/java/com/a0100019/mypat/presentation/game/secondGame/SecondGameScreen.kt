package com.a0100019.mypat.presentation.game.secondGame

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.loading.LoadingSideEffect
import com.a0100019.mypat.presentation.loading.LoadingState
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
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
        score = secondGameState.score,
        time = secondGameState.time,
        gameState = secondGameState.gameState,
        targetList = secondGameState.targetList,
        goalList = secondGameState.goalList,
        onItemSelected = secondGameViewModel::onItemSelected,
        onGameStartClick = secondGameViewModel::onGameStartClick,
        onNextLevelClick = secondGameViewModel::onNextLevelClick
    )
}



@Composable
fun SecondGameScreen(
    score : Int,
    time : Double,
    gameState : String,
    targetList : List<Int>,
    goalList : List<Int>,
    onItemSelected : (Int) -> Unit,
    onGameStartClick : () -> Unit,
    onNextLevelClick : () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(score.toString())

        Text(time.toString())
        
        Box(
            modifier = Modifier
                .size(100.dp)
                .aspectRatio(1f) // 정사각형 유지
                .padding(4.dp)
                .background(
                    when (goalList[1]) {
                        1 -> Color.Red
                        2 -> Color.Magenta
                        3 -> Color.Green
                        4 -> Color.Blue
                        5 -> Color.Cyan
                        else -> Color.LightGray
                    },
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
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
                            else -> Color.LightGray
                        },
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
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
                                        else -> Color.LightGray
                                    }, shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onItemSelected(actualIndex) // 클릭 시 인덱스 전달
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = actualIndex.toString(), color = Color.White)
                        }
                    }
                }
            }
        }

        if(gameState == "시작"){
            Button(
                onClick = onGameStartClick
            ) {
                Text("start")
            }
        } else {
            Button(
                onClick = onNextLevelClick
            ) {
                Text("next level")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SecondGameScreenPreview() {
    MypatTheme {
        SecondGameScreen(
            score = 10000,
            time = 10.4,
            goalList = listOf(1, 2),
            targetList = listOf(1, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            onItemSelected = {},
            onGameStartClick = {},
            gameState = "진행",
            onNextLevelClick = {}
        )
    }
}