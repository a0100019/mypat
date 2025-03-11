package com.a0100019.mypat.presentation.ui.image.pat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun DialogPatImage(
    patUrl: String,
    modifier : Modifier = Modifier
) {
    // `assets` 폴더에서 Lottie 파일 로드
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(patUrl)
    )

    // LottieAnimation을 클릭 가능한 Modifier로 감쌉니다.
    LottieAnimation(
        composition = composition,
        iterations = Int.MAX_VALUE,
        modifier = modifier
    )
}
