package com.a0100019.mypat.presentation.game.secondGame

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.a0100019.mypat.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.component.XmlButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SecondGameScreen(
    secondGameViewModel: SecondGameViewModel = hiltViewModel(),
    popBackStack: () -> Unit

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
        patData = secondGameState.patData,
        userData = secondGameState.userData,
        plusLove = secondGameState.plusLove,
        onGameStartClick = secondGameViewModel::onGameStartClick,
        onGameReStartClick = secondGameViewModel::onGameReStartClick,
        popBackStack = popBackStack,
        round = secondGameState.round,
        plusTime = secondGameState.plusTime,
        mapList = secondGameState.mapList,
        onMoveClick = secondGameViewModel::onMoveClick,
        onFastMoveClick = secondGameViewModel::onFastMoveClick
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun SecondGameScreen(
    time : Double,
    gameState : String,
    patData : Pat,
    plusLove : Int,
    plusTime : Double = 0.0,
    round : Int = 1,

    userData : List<User>,
    mapList : List<String> = emptyList(),

    onGameStartClick : () -> Unit,
    onGameReStartClick: () -> Unit,
    popBackStack: () -> Unit,
    onMoveClick: (String) -> Unit = {},
    onFastMoveClick: (String) -> Unit = {}

) {

    if (gameState == "성공" || gameState == "신기록") {
        SecondGameDialog(
            onClose = onGameReStartClick,
            userData = userData,
            patData = patData,
            situation = gameState,
            time = time+plusTime,
            popBackStack = popBackStack,
            plusLove = plusLove
        )
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

            Text(
                text = String.format("%.3f", time) + "초",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 8.dp)
            )
            Text(
                text = "+ " + String.format("%.0f", plusTime) + "초",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "${(round + 1)} / 10",
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                mapList[round].chunked(5).forEachIndexed { rowIndex, rowItems -> // 행 인덱스
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
                                            '0' -> Color.LightGray
                                            '1' -> MaterialTheme.colorScheme.onErrorContainer
                                            '2' -> Color(0xFF40FF40)
                                            else -> Color(0xFFF66C6C)
                                        }, shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (item == '1') {
                                    JustImage(
                                        filePath = patData.url
                                    )
                                }
                            }
                        }
                    }
                }
            }

            when (gameState) {
                "시작" -> {
                    MainButton(
                        onClick = onGameStartClick,
                        text = "\n게임 시작\n",
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(bottom = 32.dp)
                    )
                }

                else ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            XmlButton(
                                iconResId = R.drawable.arrow,
                                onClick = { onMoveClick("up") }
                            )
                            Row {
                                XmlButton(
                                    iconResId = R.drawable.arrow,
                                    rotationDegree = 270f,
                                    onClick = { onMoveClick("left") }
                                )
                                Spacer(modifier = Modifier.size(50.dp))
                                XmlButton(
                                    iconResId = R.drawable.arrow,
                                    rotationDegree = 90f,
                                    onClick = { onMoveClick("right") }
                                )
                            }
                            XmlButton(
                                iconResId = R.drawable.arrow,
                                rotationDegree = 180f,
                                onClick = { onMoveClick("down") }
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            XmlButton(
                                iconResId = R.drawable.double_arrow,
                                rotationDegree = 270f,
                                onClick = { onFastMoveClick("up") }
                            )
                            Row {
                                XmlButton(
                                    iconResId = R.drawable.double_arrow,
                                    rotationDegree = 180f,
                                    onClick = { onFastMoveClick("left") }
                                )
                                Spacer(modifier = Modifier.size(50.dp))
                                XmlButton(
                                    iconResId = R.drawable.double_arrow,
                                    onClick = { onFastMoveClick("right") }
                                )
                            }
                            XmlButton(
                                iconResId = R.drawable.double_arrow,
                                rotationDegree = 90f,
                                onClick = { onFastMoveClick("down") }
                            )
                        }
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
            onGameStartClick = {},
            gameState = "진행",
            patData = Pat(url = ""),
            onGameReStartClick = {},
            userData = listOf(),
            round = 0,
            popBackStack = {},
            plusLove = 100,
            mapList = listOf("1300003030030300303000032")
        )
    }
}