@file:Suppress("DEPRECATION")

package com.a0100019.mypat.presentation.ui.image.pat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.etc.patEffectIndexToUrl
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun DraggablePatImage(
    worldIndex: String,
    patUrl: String,
    surfaceWidthDp: Dp,
    surfaceHeightDp: Dp,
    xFloat: Float,
    yFloat: Float,
    sizeFloat: Float,
    onClick: () -> Unit,
    effect: Int = 0,
    newFloat: (Float, Float) -> Unit,
) {

    // `assets` 폴더에서 Lottie 파일 로드
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(patUrl)
    )

    val imageSize = surfaceWidthDp * sizeFloat // 이미지 크기를 Surface 너비의 비율로 설정

    // Current position in DP
    var xOffset by remember { mutableStateOf(surfaceWidthDp * xFloat) }
    var yOffset by remember { mutableStateOf(surfaceHeightDp * yFloat) }

    // Animation state
    val lottieAnimationState = animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    if (composition != null) {
        Box(
            modifier = Modifier
                .size(imageSize)
                .offset(x = xOffset, y = yOffset)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume() // Consume the touch event

                        // Update offsets by adding the drag amount
                        xOffset += dragAmount.x.toDp()
                        yOffset += dragAmount.y.toDp()

                        newFloat(
                            with(LocalDensity) { (xOffset).toPx() / surfaceWidthDp.toPx() },
                            with(LocalDensity) { (yOffset).toPx() / surfaceHeightDp.toPx() }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {

            LottieAnimation(
                composition = composition,
                progress = lottieAnimationState.progress
            )
            JustImage(
                filePath = patEffectIndexToUrl(effect),
                modifier = Modifier.fillMaxSize()
            )
            if(worldIndex != ""){
                Text(
                    text = (worldIndex.toInt() + 1).toString(),
                )

            }

        }
    } else {
        // Placeholder while loading or on error
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...")
        }
    }
}
