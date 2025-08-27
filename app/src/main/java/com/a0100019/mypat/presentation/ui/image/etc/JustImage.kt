package com.a0100019.mypat.presentation.ui.image.etc

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCancellationBehavior
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@Composable
fun JustImage(
    filePath: String,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    repetition: Boolean = false, // 한바퀴 후 역재생 할 것인지
    isPlaying: Boolean = true
) {
    if (filePath.endsWith(".json")) {
        val composition by rememberLottieComposition(LottieCache.get(filePath))

        if (repetition) {

            val progress by animateLottieCompositionAsState(
                composition = composition,
                isPlaying = isPlaying,
                restartOnPlay = false,
                reverseOnRepeat = true,
                iterations = Int.MAX_VALUE
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = modifier,
                contentScale = contentScale
            )
        } else {
            LottieAnimation(
                composition = composition,
                iterations = Int.MAX_VALUE,
                modifier = modifier,
                contentScale = contentScale,
                isPlaying = isPlaying
            )
        }
    } else {
        val context = LocalContext.current
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(filePath) {
            bitmap = try {
                val inputStream = context.assets.open(filePath)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                null
            }
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Asset Image",
                modifier = modifier,
                contentScale = contentScale
            )
        } else {
//            Box(contentAlignment = Alignment.Center) {
//                Text("?")
//            }
        }
    }
}

object LottieCache {
    private val cache = mutableMapOf<String, LottieCompositionSpec>()

    fun get(patUrl: String): LottieCompositionSpec {
        return cache.getOrPut(patUrl) {
            LottieCompositionSpec.Asset(patUrl)
        }
    }
}
