package com.a0100019.mypat.presentation.ui.image.pat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.a0100019.mypat.data.room.pat.Pat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun AddDialogPatImage(
    patData: Pat,
    onAddPatImageClick: (String) -> Unit
) {
    // `assets` 폴더에서 Lottie 파일 로드
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(patData.url)
    )

    LottieAnimation(
        composition = composition,
        iterations = Int.MAX_VALUE,
        modifier = Modifier
            .size(50.dp)
            .clickable {
                onAddPatImageClick(patData.id.toString())
            }
    )

}
