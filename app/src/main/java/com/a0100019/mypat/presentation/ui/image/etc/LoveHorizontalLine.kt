package com.a0100019.mypat.presentation.ui.image.etc

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoveHorizontalLine(
    value: Int,
    totalValue: Int = 10000,
    plusValue: Int = 0
) {
    val valuePercentage = ((value.toFloat() - plusValue.toFloat()) / totalValue.toFloat()).coerceIn(0f, 1f)
    val plusValuePercentage = (value.toFloat() / totalValue.toFloat()).coerceIn(0f, 1f)


    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        // total
        drawLine(
            color = Color.LightGray,
            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
            end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
            strokeWidth = 8f
        )
        // plus
        drawLine(
            color = Color.Yellow,
            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
            end = androidx.compose.ui.geometry.Offset(size.width * plusValuePercentage, size.height / 2),
            strokeWidth = 8f
        )
        // value
        drawLine(
            color = Color.Cyan,
            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
            end = androidx.compose.ui.geometry.Offset(size.width * valuePercentage, size.height / 2),
            strokeWidth = 8f
        )
    }
}
