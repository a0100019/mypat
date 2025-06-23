package com.a0100019.mypat.presentation.game.thirdGame

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.component.CuteIconButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ThirdGameScreen(
    thirdGameViewModel: ThirdGameViewModel = hiltViewModel(),
    popBackStack: () -> Unit

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
        memoBoard = thirdGameState.sudokuMemoBoard,
        clickedPuzzle = thirdGameState.clickedPuzzle,
        time = thirdGameState.time,
        memoMode = thirdGameState.memoMode,
        firstBoard = thirdGameState.sudokuFirstBoard,
        level = thirdGameState.level,
        gameState = thirdGameState.gameState,
        patData = thirdGameState.patData,
        userDataList = thirdGameState.userData,
        plusLove = thirdGameState.plusLove,

        onPuzzleClick = thirdGameViewModel::onPuzzleClick,
        onNumberClick = thirdGameViewModel::onNumberClick,
        onEraserClick = thirdGameViewModel::onEraserClick,
        onMemoClick = thirdGameViewModel::onMemoClick,
        onMemoNumberClick = thirdGameViewModel::onMemoNumberClick,
        onLevelClick = thirdGameViewModel::onLevelClick,
        onStateChangeClick = thirdGameViewModel::onStateChangeClick,
        newGame = thirdGameViewModel::newGame,

        popBackStack = popBackStack,
    )
}



@SuppressLint("DefaultLocale")
@Composable
fun ThirdGameScreen(
    board: List<List<String>>,
    memoBoard: List<List<String>>,
    firstBoard: List<List<String>>,
    clickedPuzzle : String,
    time : Double,
    gameState : String,
    memoMode : Boolean,
    patData : Pat,
    userDataList : List<User>,
    level: Int,
    plusLove: Int,

    onPuzzleClick : (Int, Int) -> Unit,
    onNumberClick: (Int) -> Unit,
    onMemoClick: () -> Unit,
    onEraserClick: () -> Unit,
    onMemoNumberClick: (Int) -> Unit,
    popBackStack: () -> Unit,
    onLevelClick: (Int) -> Unit,
    onStateChangeClick: (String) -> Unit,
    newGame: () -> Unit,
) {

    when(gameState) {
        "설정" -> ThirdGameStartDialog(
            patData = patData,
            popBackStack = popBackStack,
            onLevelClick = onLevelClick
        )
        "성공" -> ThirdGameSuccessDialog(
            onClose = newGame,
            time = time,
            userData = userDataList,
            patData = patData,
            popBackStack = popBackStack,
            plusLove = plusLove,
            level = level
        )
        "newGame" -> SimpleAlertDialog(
            onConfirm = newGame,
            onDismiss = {
                onStateChangeClick("")
            },
            text = "게임을 새로 시작하시겠습니까?"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        CuteIconButton(
            text = "새로 하기",
            onClick = {
                onStateChangeClick("newGame")
            },
            modifier = Modifier
                .align(Alignment.Start)
        )

        val minutes = (time / 60).toInt()
        val seconds = time % 60

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("시간 : %d분 %.2f초", minutes, seconds),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
            )

            Text(
                text = when (level) {
                    1 -> "쉬움"
                    2 -> "보통"
                    else -> "어려움"
                }
            )
        }


        Column(
            modifier = Modifier
                .border(5.dp, Color.Black) // 전체 판 테두리
                .padding(2.dp) // 테두리 여백
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            board.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                ) {
                    row.forEachIndexed { colIndex, num ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(
                                    if (rowIndex.toString() == clickedPuzzle[0].toString() || colIndex.toString() == clickedPuzzle[1].toString()) {
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
                                    if (firstBoard[rowIndex][colIndex] == "0") {
                                        onPuzzleClick(rowIndex, colIndex)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {

                            if(num != "0"){
                                Text(
                                    text = num,
                                    fontSize = 20.sp,
                                    color = if (firstBoard[rowIndex][colIndex] == "0") {
                                        Color.Blue
                                    } else {
                                        Color.Black
                                    }
                                )
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${if(memoBoard[rowIndex][colIndex].contains("1"))"1" else "  "}\n" +
                                                "${if (memoBoard[rowIndex][colIndex].contains("4")) "4" else "  "}\n" +
                                                if (memoBoard[rowIndex][colIndex].contains("7")) "7" else "  ",
                                        fontSize = 12.sp,
                                        style = TextStyle(lineHeight = 12.sp),
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${if(memoBoard[rowIndex][colIndex].contains("2"))"2" else "  "}\n" +
                                                "${if (memoBoard[rowIndex][colIndex].contains("5")) "5" else "  "}\n" +
                                                if (memoBoard[rowIndex][colIndex].contains("8")) "8" else "  ",
                                        fontSize = 12.sp,
                                        style = TextStyle(lineHeight = 12.sp),
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${if(memoBoard[rowIndex][colIndex].contains("3"))"3" else "  "}\n" +
                                                "${if (memoBoard[rowIndex][colIndex].contains("6")) "6" else "  "}\n" +
                                                if (memoBoard[rowIndex][colIndex].contains("9")) "9" else "  ",
                                        fontSize = 12.sp,
                                        style = TextStyle(lineHeight = 12.sp),
                                        color = Color.Gray
                                    )
                                }

                            }

                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ){
            Button(
                onClick = onEraserClick
            ) {
                Text(
                    text = "지우개"
                )
            }

            Button(
                onClick = onMemoClick
            ) {
                Text(
                    text = "메모"
                )
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
                            if (memoMode) {
                                onMemoNumberClick(index + 1)
                            } else {
                                onNumberClick(index + 1)
                            }
                        },
                    textAlign = TextAlign.Center // 텍스트를 가운데 정렬
                )
            }
        }

        Spacer(modifier = Modifier.size(50.dp))


    }

}

@Preview(showBackground = true)
@Composable
fun ThirdGameScreenPreview() {
    MypatTheme {
        ThirdGameScreen(
            board = Array(9) { Array(9) { "0" } }.map { it.toList() },
            firstBoard = Array(9) { Array(9) { "0" } }.map { it.toList() },
            memoBoard = Array(9) { Array(9) { "123" } }.map { it.toList() },
            clickedPuzzle = "35",
            onPuzzleClick = { row, col -> },
            onNumberClick = {},
            time = 100.1,
            memoMode = false,
            onEraserClick = {},
            onMemoClick = {},
            onMemoNumberClick = {},
            level = 3,
            gameState = "",
            patData = Pat(url = "pat/cat.json"),
            userDataList = emptyList(),
            popBackStack = {},
            onLevelClick = {},
            plusLove = 1000,
            onStateChangeClick = {},
            newGame = {}
        )
    }
}