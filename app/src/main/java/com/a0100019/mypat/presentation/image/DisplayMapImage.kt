package com.a0100019.mypat.presentation.image

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

@Composable
fun DisplayMapImage(filePath: String) {
    val context = LocalContext.current

    // Load the bitmap from assets directly inside the composable
    val bitmap = remember {
        val inputStream = context.assets.open(filePath)
        BitmapFactory.decodeStream(inputStream)
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Asset Image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds // 비율 무시하고 가득 채움
    )

}
