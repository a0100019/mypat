package com.a0100019.mypat.presentation.ui.image.item

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.a0100019.mypat.presentation.ui.image.etc.LottieCache
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun DraggableItemImage(
    worldIndex: String = "",
    itemUrl: String,
    surfaceWidthDp: Dp,
    surfaceHeightDp: Dp,
    xFloat: Float,
    yFloat: Float,
    sizeFloat: Float,
    onClick: () -> Unit,
    newFloat: (Float, Float) -> Unit,
    ) {


    val imageSize = surfaceWidthDp * sizeFloat // 이미지 크기를 Surface 너비의 비율로 설정

    // Current position in DP
    var xOffset by remember { mutableStateOf(surfaceWidthDp * xFloat) }
    var yOffset by remember { mutableStateOf(surfaceHeightDp * yFloat) }

    if(itemUrl.takeLast(4) == "json") {

        val composition by rememberLottieComposition(LottieCache.get(itemUrl))


        // LottieAnimation을 클릭 가능한 Modifier로 감쌉니다.
        Box(
            modifier = Modifier
                .size(imageSize)
                .offset(x = xOffset, y = yOffset)
                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null,
                    onClick = onClick
                )
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume() // Consume the touch event

                        // Update offsets by adding the drag amount
                        xOffset += dragAmount.x.toDp()
                        yOffset += dragAmount.y.toDp()

                        newFloat(
                            with(LocalDensity) { (xOffset).toPx() / surfaceWidthDp.toPx() },
                            with(LocalDensity) { (yOffset).toPx() / surfaceHeightDp.toPx() }
                        )
                    }
                }
            ,
            contentAlignment = Alignment.Center
        ){
            LottieAnimation(
                composition = composition,
                iterations = Int.MAX_VALUE,
            )
//            Text(
//                text = (worldIndex.toInt() + 1).toString()
//            )
        }

    } else {
        val context = LocalContext.current

        // State to hold the bitmap
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        // Load the bitmap whenever filePath changes
        LaunchedEffect(itemUrl) {
            bitmap = try {
                val inputStream = context.assets.open(itemUrl)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                null // Handle errors gracefully
            }
        }

        if (bitmap != null) {
            Box(
                modifier = Modifier
                    .size(imageSize)
                    .offset(x = xOffset, y = yOffset)
                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null,
                        onClick = onClick
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume() // Consume the touch event

                            // Update offsets by adding the drag amount
                            xOffset += dragAmount.x.toDp()
                            yOffset += dragAmount.y.toDp()

                            newFloat(
                                with(LocalDensity) { (xOffset).toPx() / surfaceWidthDp.toPx() },
                                with(LocalDensity) { (yOffset).toPx() / surfaceHeightDp.toPx() }
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Asset Image",
                )
//                if(worldIndex != ""){
//                    Text(
//                        text = (worldIndex.toInt() + 1).toString()
//                    )
//                }
            }
        } else {
            // Placeholder while loading or on error
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        }
    }

}
