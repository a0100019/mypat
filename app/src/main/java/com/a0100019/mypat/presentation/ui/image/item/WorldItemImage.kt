package com.a0100019.mypat.presentation.ui.image.item

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.a0100019.mypat.presentation.ui.image.etc.LottieCache
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun WorldItemImage(
    itemUrl: String,
    surfaceWidthDp: Dp,
    surfaceHeightDp: Dp,
    xFloat: Float,
    yFloat: Float,
    sizeFloat: Float,
    onClick: () -> Unit = {}
) {
    val imageSize = surfaceWidthDp * sizeFloat

    // 클릭 효과 제거용 Modifier
    val noEffectClickable = Modifier
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }

    if (itemUrl.takeLast(4) == "json") {

        val composition by rememberLottieComposition(LottieCache.get(itemUrl))

        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE,
            modifier = Modifier
                .size(imageSize)
                .offset(
                    x = (surfaceWidthDp * xFloat),
                    y = (surfaceHeightDp * yFloat)
                )
                .then(noEffectClickable)  // ← 클릭 효과 제거
        )

    } else {
        val context = LocalContext.current
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(itemUrl) {
            bitmap = try {
                val inputStream = context.assets.open(itemUrl)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                null
            }
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(imageSize)
                    .offset(
                        x = (surfaceWidthDp * xFloat),
                        y = (surfaceHeightDp * yFloat)
                    )
                    .then(noEffectClickable) // ← 클릭 효과 제거
            )
        } else {
            Box(
                modifier = Modifier
                    .size(imageSize)
                    .offset(
                        x = (surfaceWidthDp * xFloat),
                        y = (surfaceHeightDp * yFloat)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        }
    }
}
