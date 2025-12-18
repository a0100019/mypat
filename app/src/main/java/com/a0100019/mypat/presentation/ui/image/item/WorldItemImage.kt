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
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun WorldItemImage(
    itemUrl: String,
    surfaceWidthDp: Dp,
    surfaceHeightDp: Dp,
    xFloat: Float,
    yFloat: Float,
    sizeFloat: Float,
    isPlaying: Boolean = true,
    onClick: () -> Unit = {}
) {
    val imageSize = remember(surfaceWidthDp, sizeFloat) {
        surfaceWidthDp * sizeFloat
    }

    val modifier = remember(
        surfaceWidthDp,
        surfaceHeightDp,
        xFloat,
        yFloat,
        imageSize
    ) {
        Modifier
            .size(imageSize)
            .offset(
                x = surfaceWidthDp * xFloat,
                y = surfaceHeightDp * yFloat
            )
    }

    val noEffectClickable = remember {
        Modifier.clickable(
            interactionSource = MutableInteractionSource(),
            indication = null,
            onClick = onClick
        )
    }

    // =========================
    // 🔥 LOTTIE ITEM
    // =========================
    if (itemUrl.endsWith(".json")) {

        val composition by rememberLottieComposition(LottieCache.get(itemUrl))

        // 애니메이션 진행 중일 때만 계산
        val animatedProgress by animateLottieCompositionAsState(
            composition = composition,
            isPlaying = isPlaying,
            iterations = Int.MAX_VALUE
        )

        // 멈춘 시점 progress 고정
        val frozenProgress = remember { mutableStateOf(0f) }

        if (isPlaying) {
            frozenProgress.value = animatedProgress
        }

        LottieAnimation(
            composition = composition,
            progress = {
                if (isPlaying) animatedProgress else frozenProgress.value
            },
            modifier = modifier.then(noEffectClickable)
        )

    } else {
        // =========================
        // 🖼️ BITMAP ITEM
        // =========================
        val context = LocalContext.current
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(itemUrl) {
            bitmap = try {
                context.assets.open(itemUrl).use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (e: Exception) {
                null
            }
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = modifier.then(noEffectClickable)
            )
        } else {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        }
    }
}
