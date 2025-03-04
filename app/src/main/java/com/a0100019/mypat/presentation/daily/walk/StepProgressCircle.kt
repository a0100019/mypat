package com.a0100019.mypat.presentation.daily.walk

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StepProgressCircle(
    steps: Int,
    goal: Int = 10000,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier.fillMaxSize()) {
    val progress = (steps.toFloat() / goal).coerceIn(0f, 1f) // ✅ 0~1 범위로 정규화
    val sweepAngle = progress * 360 // ✅ 채울 각도 (0~360도)

    Canvas(
        modifier = modifier
    ) {
        val size = size.minDimension
        val strokeWidth = size * 0.05f // ✅ 원 테두리 두께 설정

        // ✅ 배경 원 테두리 (회색)
        drawArc(
            color = Color.LightGray,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )

        // ✅ 진행 상황 원 테두리 (초록색)
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xff63C6C4), Color(0xff97CA49)
                ),
            ),
            startAngle = 270f, // ✅ 12시 방향에서 시작
            sweepAngle = sweepAngle, // ✅ 진행 정도에 따른 각도
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStepProgress() {
        StepProgressCircle(
            steps = 5000
        ) // ✅ 5000 걸음 (반 채워짐)
}
