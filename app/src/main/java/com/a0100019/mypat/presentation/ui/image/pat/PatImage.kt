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
    isPlaying: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    // composition 캐싱
    val composition by rememberLottieComposition(LottieCache.get(patUrl))

    val imageSize = remember(surfaceWidthDp, sizeFloat) {
        surfaceWidthDp * sizeFloat
    }

    // effect 계산도 고정
    PatEffectImage(
        surfaceWidthDp = surfaceWidthDp,
        surfaceHeightDp = surfaceHeightDp,
        effect = effect,
        xFloat = xFloat,
        yFloat = yFloat,
        sizeFloat = sizeFloat,
        isPlaying = isPlaying
    )

    // modifier 고정
    val modifier = remember(
        surfaceWidthDp,
        surfaceHeightDp,
        xFloat,
        yFloat,
        imageSize,
        onClick
    ) {
        Modifier
            .size(imageSize)
            .offset(
                x = surfaceWidthDp * xFloat,
                y = surfaceHeightDp * yFloat
            )
            .let {
                if (onClick != null) {
                    it.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onClick
                    )
                } else it
            }
    }

    // 🔥 핵심: 멈춰 있을 때 progress 계산 완전 중단
    val animatedProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = Int.MAX_VALUE
    )

    // 멈춘 순간의 progress 고정
    val frozenProgress = remember { androidx.compose.runtime.mutableStateOf(0f) }

    if (isPlaying) {
        frozenProgress.value = animatedProgress
    }

    LottieAnimation(
        composition = composition,
        progress = {
            if (isPlaying) animatedProgress else frozenProgress.value
        },
        modifier = modifier
    )
}
