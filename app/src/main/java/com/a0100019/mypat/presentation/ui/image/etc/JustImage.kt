package com.a0100019.mypat.presentation.ui.image.etc

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun JustImage(
    filePath: String,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    contentScale:ContentScale = ContentScale.Fit
) {

    if(filePath.takeLast(4) == "json") {

        // `assets` 폴더에서 Lottie 파일 로드
        val composition by rememberLottieComposition(
            LottieCompositionSpec.Asset(filePath)
        )

        // LottieAnimation을 클릭 가능한 Modifier로 감쌉니다.
        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE,
            modifier = modifier,
            contentScale = contentScale
        )

    } else {
        val context = LocalContext.current


        // State to hold the bitmap
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }


//
        // Load the bitmap whenever filePath changes
        LaunchedEffect(filePath) {
            bitmap = try {
                val inputStream = context.assets.open(filePath)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                null // Handle errors gracefully
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
            // Placeholder while loading or on error
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text("?")
            }
        }
    }
}
