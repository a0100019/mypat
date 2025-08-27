package com.a0100019.mypat.presentation.ui.image.pat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.a0100019.mypat.presentation.ui.image.etc.LottieCache
import com.a0100019.mypat.presentation.ui.image.etc.PatEffectImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun PatInformationImage(
    patUrl: String,
    surfaceWidthDp: Dp,
    surfaceHeightDp: Dp,
    xFloat: Float,
    yFloat: Float,
    sizeFloat: Float,
    effect: Int = 0
) {

    val composition by rememberLottieComposition(LottieCache.get(patUrl))

    val imageSize = surfaceWidthDp * sizeFloat // 이미지 크기를 Surface 너비의 비율로 설정

    // LottieAnimation을 클릭 가능한 Modifier로 감쌉니다.
    LottieAnimation(
        composition = composition,
        iterations = Int.MAX_VALUE,
        modifier = Modifier
            .size(imageSize)
            .offset(
                x = (surfaceWidthDp * xFloat),
                y = (surfaceHeightDp * yFloat)
            )
    )

    PatEffectImage(
        surfaceWidthDp = surfaceWidthDp,
        surfaceHeightDp = surfaceHeightDp,
        effect = effect,
        xFloat = xFloat,
        yFloat = yFloat,
        sizeFloat = sizeFloat,
    )
}