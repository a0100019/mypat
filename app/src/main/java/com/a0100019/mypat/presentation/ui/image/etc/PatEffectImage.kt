package com.a0100019.mypat.presentation.ui.image.etc

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
fun PatEffectImage(
    surfaceWidthDp: Dp,
    surfaceHeightDp: Dp,
    effect: Int = 0,
    xFloat: Float,
    yFloat: Float,
    sizeFloat: Float,
) {

    val imageUrl = patEffectIndexToUrl(effect)

    // `assets` 폴더에서 Lottie 파일 로드
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(imageUrl)
    )

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

}

fun patEffectIndexToUrl(index: Int): String {
    return when(index) {
        1 -> "patEffect/leaf.json"
        else -> "etc/egg.json"
    }
}