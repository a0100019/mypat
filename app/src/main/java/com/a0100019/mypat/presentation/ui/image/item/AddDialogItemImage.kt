package com.a0100019.mypat.presentation.ui.image.item

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pet.Pat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun AddDialogItemImage(
    itemData: Item,
    onAddItemImageClick: (String) -> Unit
) {
    val context = LocalContext.current

    // State to hold the bitmap
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load the bitmap whenever filePath changes
    LaunchedEffect(itemData.url) {
        bitmap = try {
            val inputStream = context.assets.open(itemData.url)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null // Handle errors gracefully
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Asset Image",
            modifier = Modifier
                .clickable {
                    onAddItemImageClick(itemData.id.toString())
                }
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
