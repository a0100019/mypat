package com.a0100019.mypat.presentation.ui.image.item

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

//아이템 이미지 가져오는 코드
@Composable
fun WorldItemImage(
    itemUrl: String,
    surfaceWidthDp: Dp,
    surfaceHeightDp: Dp,
    xFloat: Float,
    yFloat: Float,
    sizeFloat: Float
) {
    if(itemUrl.takeLast(4) == "json") {

        val imageSize = surfaceWidthDp * sizeFloat // 이미지 크기를 Surface 너비의 비율로 설정

        // `assets` 폴더에서 Lottie 파일 로드
        val composition by rememberLottieComposition(
            LottieCompositionSpec.Asset(itemUrl)
        )

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

    } else {
        val context = LocalContext.current


        // State to hold the bitmap
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }


//
        // Load the bitmap whenever filePath changes
        LaunchedEffect(itemUrl) {
            bitmap = try {
                val inputStream = context.assets.open(itemUrl)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                null // Handle errors gracefully
            }
        }

        val imageSize = surfaceWidthDp * sizeFloat // 이미지 크기를 Surface 너비의 비율로 설정

        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Asset Image",
                modifier = Modifier
                    .size(imageSize)
                    .offset(
                        x = (surfaceWidthDp * xFloat),
                        y = (surfaceHeightDp * yFloat)
                    )
            )
        } else {
            // Placeholder while loading or on error
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        }
    }
}
