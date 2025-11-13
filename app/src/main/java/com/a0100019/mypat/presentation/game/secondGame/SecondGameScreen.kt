package com.a0100019.mypat.presentation.game.secondGame

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import com.a0100019.mypat.presentation.ui.MusicPlayer
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
        onGameReStartClick = secondGameViewModel::onGameReStartClick,
        popBackStack = popBackStack,
        targetNumber = secondGameState.targetNumber,
        plusTime = secondGameState.plusTime,
        firstNumberList = secondGameState.firstNumberList,
        secondNumberList = secondGameState.secondNumberList,
        stateList = secondGameState.stateList,
        onIndexClick = secondGameViewModel::onIndexClick
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
    targetNumber : Int = 1,

    userData : List<User>,
    firstNumberList : List<Int> = emptyList(),
    secondNumberList : List<Int> = emptyList(),
    stateList : List<String> = emptyList(),

    onGameReStartClick: () -> Unit,
    popBackStack: () -> Unit,
    onIndexClick: (Int) -> Unit = {}


) {

    val context = LocalContext.current

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

    when(gameState) {
        "성공" -> MusicPlayer(
            id = R.raw.positive10
        )
        "신기록" -> MusicPlayer(
            id = R.raw.congratulation
        )
        "진행" -> MusicPlayer(
            id = R.raw.bell2
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
                text = "타겟 숫자 : $targetNumber",
                style = MaterialTheme.typography.headlineMedium
            )


            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp, end = 6.dp),
                verticalArrangement = Arrangement.Center
            ) {
                (0..24).chunked(5).forEachIndexed { rowIndex, rowItems ->
                    Row {
                        rowItems.forEachIndexed { columnIndex, item ->

                            val actualIndex = rowIndex * 5 + columnIndex // 실제 인덱스

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .background(
                                        when (stateList[actualIndex]) {
                                            "0" -> MaterialTheme.colorScheme.onErrorContainer
                                            "1" -> MaterialTheme.colorScheme.error
                                            "2" -> Color.LightGray
                                            else -> Color.LightGray
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        onIndexClick(actualIndex)
                                    }
                                ,
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (stateList[actualIndex]) {
                                        "0" -> firstNumberList[actualIndex].toString()
                                        "1" -> secondNumberList[actualIndex].toString()
                                        else -> ""
                                    }
                                    ,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }

            }

            Spacer(modifier = Modifier.size(100.dp))

            Text(
                text = "1부터 순서대로 누르세요!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 6.dp)
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondGameScreenPreview() {
    MypatTheme {
        SecondGameScreen(
            time = 10.4,
            gameState = "진행",
            patData = Pat(url = ""),
            onGameReStartClick = {},
            userData = listOf(),
            popBackStack = {},
            plusLove = 100,
            stateList = List(25) { "0" },
            firstNumberList = (1..25).shuffled().toList(),
            secondNumberList = (26..50).toList(),

        )
    }
}