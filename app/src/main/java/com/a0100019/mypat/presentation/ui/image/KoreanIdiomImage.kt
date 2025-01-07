package com.a0100019.mypat.presentation.ui.image

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
fun KoreanIdiomImage(filePath: String) {
    val context = LocalContext.current

    // Load the bitmap from assets directly inside the composable
    val bitmap = remember {
        val inputStream = context.assets.open(filePath)
        BitmapFactory.decodeStream(inputStream)
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Asset Image",
        modifier = Modifier
            .fillMaxSize()
//            .fillMaxWidth(0.5f) // 부모 너비의 50%
//            .aspectRatio(0.5f)   // 정사각형 비율 유지
    )
}
