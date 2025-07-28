package com.a0100019.mypat.presentation.ui.image.etc

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun LoveHorizontalLine(
    value: Int,
    totalValue: Int = 10000,
    plusValue: Int = 0,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val nowValue = value % 10000
    val yellowValue = (nowValue + plusValue).coerceAtMost(10000)
    val valuePercentage by animateFloatAsState(
        targetValue = nowValue.toFloat() / totalValue.toFloat(),
        label = "baseProgress"
    )
    val plusValuePercentage by animateFloatAsState(
        targetValue = yellowValue.toFloat() / totalValue.toFloat(),
        label = "plusProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF3E5F5)) // 전체 배경: 연보라
    ) {
        // Plus Progress (노란색)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(plusValuePercentage)
                .background(Color(0xFFFFF176)) // 노랑
        )

        // Base Progress (파란색)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(valuePercentage)
                .background(Color(0xFF80DEEA)) // 시안
        )

    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun LoveHorizontalLinePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        LoveHorizontalLine(
            value = 16700,
            plusValue = 200, // 5500 + 1200 = 6700
            totalValue = 10000
        )
    }
}
