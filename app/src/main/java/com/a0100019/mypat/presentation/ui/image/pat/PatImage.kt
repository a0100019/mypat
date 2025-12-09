package com.a0100019.mypat.presentation.ui.image.pat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.a0100019.mypat.presentation.ui.image.etc.LottieCache
import com.a0100019.mypat.presentation.ui.image.etc.PatEffectImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun PatImage(
    patUrl: String,
    surfaceWidthDp: Dp,
    surfaceHeightDp: Dp,
    effect: Int = 0,
    xFloat: Float,
    yFloat: Float,
    sizeFloat: Float,
    isPlaying: Boolean = true,          // ✅ 애니메이션 ON/OFF 변수
    onClick: (() -> Unit)? = null
) {
    val composition by rememberLottieComposition(LottieCache.get(patUrl))

    val imageSize = surfaceWidthDp * sizeFloat

    PatEffectImage(
        surfaceWidthDp = surfaceWidthDp,
        surfaceHeightDp = surfaceHeightDp,
        effect = effect,
        xFloat = xFloat,
        yFloat = yFloat,
        sizeFloat = sizeFloat,
    )

    val modifier = Modifier
        .size(imageSize)
        .offset(
            x = (surfaceWidthDp * xFloat),
            y = (surfaceHeightDp * yFloat)
        )
        .let {
            if (onClick != null) {
                it.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
            } else it
        }

    // ✅ 애니메이션 상태 제어
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = if (isPlaying) Int.MAX_VALUE else 1
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}
