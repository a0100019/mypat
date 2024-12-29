package com.a0100019.mypat.presentation.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.a0100019.mypat.data.image.loadBitmapFromAssets

@Composable
fun DisplayKoreanIdiomImage(filePath: String) {
    val context = LocalContext.current
    val bitmap = remember { loadBitmapFromAssets(context, filePath) } // Load the bitmap only once

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Asset Image",
        modifier = Modifier
            .fillMaxWidth(0.5f) // 부모 너비의 50%
            .aspectRatio(1f)   // 정사각형 비율 유지
    )
}