package com.a0100019.mypat.presentation.ui.image.etc

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.a0100019.mypat.data.room.area.Area
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun AddDialogMapImage(
    areaData: Area,
    onAddMapImageClick: (String) -> Unit
) {
    if (areaData.url.takeLast(4) == "json") {

        val composition by rememberLottieComposition(LottieCache.get(areaData.url))

        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE,
            modifier = Modifier
                .fillMaxWidth() // 원하는 크기 비율로 조절
                .aspectRatio(1f / 1.25f) // 🔹 1:1.25 비율 강제
                .clickable {
                    onAddMapImageClick(areaData.id.toString())
                }
        )

    } else {
        val context = LocalContext.current
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(areaData.url) {
            bitmap = try {
                val inputStream = context.assets.open(areaData.url)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                null
            }
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Asset Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1.25f) // 🔹 동일하게 1:1.25 강제
                    .clickable {
                        onAddMapImageClick(areaData.id.toString())
                    },
                contentScale = ContentScale.FillBounds // 이미지가 꽉 차도록
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1.25f),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        }
    }
}
