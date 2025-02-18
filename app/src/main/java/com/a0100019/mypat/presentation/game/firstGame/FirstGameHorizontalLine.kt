package com.a0100019.mypat.presentation.game.firstGame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FirstGameHorizontalLine(value: Int) {
    val percentage = (value.toFloat() / 1000f).coerceIn(0f, 1f)


    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        // 전체 선
        drawLine(
            color = Color.LightGray,
            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
            end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
            strokeWidth = 4f
        )
        // 값에 따른 선
        drawLine(
            color = Color.Cyan,
            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
            end = androidx.compose.ui.geometry.Offset(size.width * percentage, size.height / 2),
            strokeWidth = 8f
        )
    }
}