package com.a0100019.mypat.presentation.ui.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.unit.Dp

//아이템 이미지 가져오는 코드
@Composable
fun ItemImage(
    itemUrl: String,
    surfaceWidthDp: Dp,
    surfaceHeightDp: Dp,
    xFloat: Float,
    yFloat: Float,
    sizeFloat: Float
) {
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

    val imageSize = surfaceWidthDp * sizeFloat // 이미지 크기를 Surface 너비의 비율로 설정

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Asset Image",
            modifier = Modifier
                .size(imageSize)
                .offset(
                    x = (surfaceWidthDp * xFloat) - (imageSize / 2),
                    y = (surfaceHeightDp * yFloat) - (imageSize / 2)
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

//
//@Composable
//fun DraggableItemImage(
//    itemUrl: String,
//    surfaceWidthDp: Dp,
//    surfaceHeightDp: Dp,
//    initialXFloat: Float,
//    initialYFloat: Float,
//    sizeFloat: Float,
//    onPositionChange: (Float, Float) -> Unit
//) {
//    val context = LocalContext.current
//
//    // State to hold the bitmap
//    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
//
//    // Current position
//    var xFloat by remember { mutableStateOf(initialXFloat) }
//    var yFloat by remember { mutableStateOf(initialYFloat) }
//
//    // Load the bitmap whenever filePath changes
//    LaunchedEffect(itemUrl) {
//        bitmap = try {
//            val inputStream = context.assets.open(itemUrl)
//            BitmapFactory.decodeStream(inputStream)
//        } catch (e: Exception) {
//            null // Handle errors gracefully
//        }
//    }
//
//    val imageSize = surfaceWidthDp * sizeFloat // 이미지 크기를 Surface 너비의 비율로 설정
//
//    if (bitmap != null) {
//        Image(
//            bitmap = bitmap!!.asImageBitmap(),
//            contentDescription = "Asset Image",
//            modifier = Modifier
//                .size(imageSize)
//                .offset(
//                    x = (surfaceWidthDp * xFloat) - (imageSize / 2),
//                    y = (surfaceHeightDp * (1f - yFloat)) - (imageSize / 2)
//                )
//                .pointerInput(Unit) {
//                    detectDragGestures { change, dragAmount ->
//                        change.consume() // 터치 이벤트 소모
//                        val deltaX = dragAmount.x / surfaceWidthDp.value // 드래그한 거리 비율로 계산
//                        val deltaY = dragAmount.y / surfaceHeightDp.value
//
//                        // xFloat, yFloat 업데이트
//                        xFloat = (xFloat + deltaX).coerceIn(0f, 1f) // 0~1 사이 값으로 제한
//                        yFloat = (yFloat - deltaY).coerceIn(0f, 1f) // 0~1 사이 값으로 제한 (위아래 반전)
//
//                        // 외부로 변경된 위치 전달
//                        onPositionChange(xFloat, yFloat)
//                    }
//                }
//        )
//    } else {
//        // Placeholder while loading or on error
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text("Loading...")
//        }
//    }
//}
//
