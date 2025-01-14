package com.a0100019.mypat.presentation.ui.image.etc

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

//맵 이미지 가져오는 코드
@Composable
fun MapImage(filePath: String) {
    val context = LocalContext.current

    // State to hold the bitmap
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

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
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // 비율 무시하고 가득 채움
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
