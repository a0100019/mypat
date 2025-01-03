package com.a0100019.mypat.presentation.image

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun PatImage(
    patUrl: String,
    surfaceWidthDp: Dp,
    surfaceHeightDp: Dp,
    xFloat: Float,
    yFloat: Float,
    sizeFloat: Float
) {
    // `assets` 폴더에서 Lottie 파일 로드
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(patUrl)
    )

    val imageSize = surfaceWidthDp * sizeFloat // 이미지 크기를 Surface 너비의 비율로 설정

    LottieAnimation(
        composition = composition,
        iterations = Int.MAX_VALUE,
        modifier = Modifier
            .size(imageSize)
            .offset(
                x = (surfaceWidthDp * xFloat) - (imageSize / 2),
                y = (surfaceHeightDp * (1f- yFloat)) - (imageSize / 2)
            )
    )
}
