package com.a0100019.mypat.presentation.game.firstGame

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun FirstGameScreen(
    firstGameViewModel: FirstGameViewModel = hiltViewModel()

) {

    val firstGameState : FirstGameState = firstGameViewModel.collectAsState().value

    val context = LocalContext.current

    firstGameViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FirstGameSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    FirstGameScreen(
        snowballX = firstGameState.snowballX,
        snowballY = firstGameState.snowballY,
        rotationAngle = firstGameState.rotationAngle,
        shotDuration = firstGameState.shotDuration,
        snowballSize = firstGameState.snowballSize,
        targetSize = firstGameState.targetSize,
        score = firstGameState.score,
        targetX = firstGameState.targetX,
        targetY = firstGameState.targetY,
        level = firstGameState.level,
        situation = firstGameState.situation,
        userData = firstGameState.userData,
        rotationDuration = firstGameState.rotationDuration,
        shotPower = firstGameState.shotPower,
        patData = firstGameState.patData,


        onGameStartClick = firstGameViewModel::onGameStartClick,
        onMoveClick = firstGameViewModel::onMoveClick,
        onRotateStopClick = firstGameViewModel::onRotateStopClick,
        onNextLevelClick = firstGameViewModel::onNextLevelClick,
        onGameReStartClick = firstGameViewModel::onGameReStartClick
    )
}



@Composable
fun FirstGameScreen(
    snowballX : Dp,
    snowballY : Dp,
    rotationAngle : Float,
    shotDuration : Int,
    snowballSize : Dp,
    targetSize : Dp,
    score : Int,
    targetX : Dp,
    targetY : Dp,
    level: Int,
    situation: String,
    userData : List<User>,
    rotationDuration: Int,
    shotPower : Int,
    patData : Pat,

    onGameReStartClick: () -> Unit,
    onGameStartClick : (Dp, Dp) -> Unit,
    onMoveClick: () -> Unit,
    onRotateStopClick: () -> Unit,
    onNextLevelClick: () -> Unit,
) {
    // 부드러운 애니메이션 적용
    val animatedX by animateDpAsState(
        targetValue = snowballX,
        animationSpec = tween(durationMillis = shotDuration, easing = LinearEasing),
        label = "" // 속도 변화에 맞춰 애니메이션 적용
    )
    val animatedY by animateDpAsState(
        targetValue = snowballY,
        animationSpec = tween(durationMillis = shotDuration, easing = LinearEasing),
        label = "" // 속도 변화에 맞춰 애니메이션 적용
    )
    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = rotationDuration, easing = LinearEasing),
        label = "" // 0.3초 동안 부드럽게 회전
    )

    if (situation == "종료" || situation == "신기록") {
        GameOverDialog (
            onClose = onGameReStartClick,
            score = score,
            level = level,
            userData = userData,
            patData = patData,
            situation = situation
        )
    }

    Column {
        Text("점수 : $score")
        Text("최고 기록 : ${userData.find { it.id == "firstGame" }?.value}")
        Text("레벨 : $level")

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth()
//                .border(10.dp, Color.Black)
                .padding(16.dp), // Optional: Set background color
//            contentAlignment = Alignment.Center // Center content
        ) {

            val density = LocalDensity.current

            // Surface 크기 가져오기 (px → dp 변환)
            val surfaceWidth = constraints.maxWidth
            val surfaceHeight = constraints.maxHeight

            val surfaceWidthDp = with(density) { surfaceWidth.toDp() }
            val surfaceHeightDp = with(density) { surfaceHeight.toDp() }

            JustImage("etc/icySurface_white_bg.jpg")
            if(situation == "회전" || situation == "준비"){
                JustImage(
                    filePath = "etc/arrow.png",
                    modifier = Modifier
                        .size(30.dp, 80.dp)
                        .offset(x = animatedX, y = animatedY - 25.dp)
                        .rotate(animatedRotation)
                )
            }
            JustImage(
                filePath = "etc/snowball.png",
                modifier = Modifier
                    .size(snowballSize)
                    .offset(x = animatedX, y = animatedY)
            )
            JustImage(
                filePath = "etc/target.png",
                modifier = Modifier
                    .size(targetSize)
                    .offset(x = targetX, y = targetY)
            )

            if(situation == "시작"){
                Button(
                    onClick = {
                        onGameStartClick(surfaceWidthDp, surfaceHeightDp)
                    }
                ) {
                    Text("시작하기")
                }
            }
        }

        when (situation) {
            "회전" -> {
                Button(
                    onClick = onRotateStopClick
                ) {
                    Text("정지")
                }

            }
            "준비" -> {
                Column {
                    FirstGameHorizontalLine(shotPower)
                    Button(
                        onClick = onMoveClick
                    ) {
                        Text("슛")
                    }
                }

            }
            "다음" -> {
                Button(
                    onClick = onNextLevelClick
                ) {
                    Text("다음 레벨")
                }
            }
        }

        Text(shotPower.toString())


    }

}

@Preview(showBackground = true)
@Composable
fun FirstGameScreenPreview() {
    MypatTheme {
        FirstGameScreen(
            snowballX = 0.dp,
            snowballY = 0.dp,
            rotationAngle = 0f,
            shotDuration = 0,
            snowballSize = 30.dp,
            targetSize = 100.dp,
            score = 0,
            targetX = 0.dp,
            targetY = 0.dp,
            level = 1,
            situation = "준비",
            userData = emptyList(),
            rotationDuration = 0,
            shotPower = 0,
            patData = Pat(url = ""),

            onGameStartClick = { x, y -> },
            onMoveClick = {},
            onRotateStopClick = {},
            onNextLevelClick = {},
            onGameReStartClick = {}
        )
    }
}