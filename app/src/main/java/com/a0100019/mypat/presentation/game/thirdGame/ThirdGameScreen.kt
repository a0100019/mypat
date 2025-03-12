package com.a0100019.mypat.presentation.game.thirdGame

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.game.secondGame.SecondGameSideEffect
import com.a0100019.mypat.presentation.game.secondGame.SecondGameState
import com.a0100019.mypat.presentation.game.secondGame.SecondGameViewModel
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ThirdGameScreen(
    thirdGameViewModel: ThirdGameViewModel = hiltViewModel()

) {

    val thirdGameState : ThirdGameState = thirdGameViewModel.collectAsState().value

    val context = LocalContext.current

    thirdGameViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ThirdGameSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    ThirdGameScreen(
        board = thirdGameState.sudokuBoard,
        clickedPuzzle = thirdGameState.clickedPuzzle,
        time = thirdGameState.time,
        onStartClick = thirdGameViewModel::makeSudoku,
        onPuzzleClick = thirdGameViewModel::onPuzzleClick,
        onNumberClick = thirdGameViewModel::onNumberClick
    )
}



@Composable
fun ThirdGameScreen(
    board: List<List<Int>>,
    clickedPuzzle : String,
    time : Double,
    onStartClick: () -> Unit,
    onPuzzleClick : (Int, Int) -> Unit,
    onNumberClick: (Int) -> Unit,
) {


    Column {

        Text(
            text = String.format("%.2f", time)
        )

        Button(
            onClick = onStartClick
        ) {
            Text("표작성")
        }

        Column(
            modifier = Modifier
                .border(5.dp, Color.Black) // 전체 판 테두리
                .padding(2.dp) // 테두리 여백
        ) {
            board.forEachIndexed { rowIndex, row ->
                Row {
                    row.forEachIndexed { colIndex, num ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if(rowIndex.toString() == clickedPuzzle[0].toString() || colIndex.toString() == clickedPuzzle[1].toString()) {
                                        Color.LightGray
                                    } else {
                                        Color.White
                                    }
                                )
                                .drawBehind {
                                    val borderSize = 1.dp.toPx()
                                    val thickBorder = 3.dp.toPx()
                                    drawLine(
                                        color = Color.Black,
                                        start = Offset(0f, size.height), // 아래쪽 시작점
                                        end = Offset(size.width, size.height), // 아래쪽 끝점
                                        strokeWidth = if ((rowIndex + 1) % 3 == 0) thickBorder else borderSize
                                    )
                                    drawLine(
                                        color = Color.Black,
                                        start = Offset(size.width, 0f), // 오른쪽 시작점
                                        end = Offset(size.width, size.height), // 오른쪽 끝점
                                        strokeWidth = if ((colIndex + 1) % 3 == 0) thickBorder else borderSize
                                    )
                                }
                                .clickable {
                                    onPuzzleClick(rowIndex, colIndex)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = num.toString(),
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // 텍스트를 가로로 가운데 정렬
        ) {
            repeat(9) { index ->
                Text(
                    text = (index + 1).toString(),
                    fontSize = 30.sp,
                    modifier = Modifier
                        .weight(1f) // 각 텍스트가 동일한 너비를 차지
                        .clickable {
                            onNumberClick(index + 1)
                        },
                    textAlign = TextAlign.Center // 텍스트를 가운데 정렬
                )
            }
        }


    }

}

@Preview(showBackground = true)
@Composable
fun ThirdGameScreenPreview() {
    MypatTheme {
        ThirdGameScreen(
            board = Array(9) { IntArray(9) { 1 } }.map { it.toList() },
            onStartClick = {},
            clickedPuzzle = "35",
            onPuzzleClick = { row, col -> },
            onNumberClick = {},
            time = 100.1
        )
    }
}