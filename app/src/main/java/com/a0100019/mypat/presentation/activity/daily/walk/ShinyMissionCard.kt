package com.a0100019.mypat.presentation.activity.daily.walk

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun ShinyMissionCard(
    onClick: () -> Unit = {}
) {
    // 애니메이션을 위한 각도
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing)
        ), label = ""
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFF176), // Pastel Yellow
            Color(0xFFFFF8E1), // Very light pastel (like pastel white)
            Color(0xFFB2EBF2)  // Pastel Cyan
        ),
        start = Offset.Zero,
        end = Offset(
            x = cos(Math.toRadians(angle.toDouble())).toFloat() * 300f,
            y = sin(Math.toRadians(angle.toDouble())).toFloat() * 300f
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(2.dp, Color(0xFFFFF176), shape = MaterialTheme.shapes.medium) // 연노랑 테두리
            .shadow(8.dp, shape = MaterialTheme.shapes.medium) // 그림자
            .background(shimmerBrush, shape = MaterialTheme.shapes.medium) // 반짝이는 배경
            .padding(16.dp)
            .clickable(
                indication = null, // 클릭 효과 제거
                interactionSource = remember { MutableInteractionSource() } // 꼭 넣어줘야 함
            ) {
                onClick()
            }
    )
    {
        Text(
            text = "클릭하여 햇살을 획득하세요!",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
            ,
            textAlign = TextAlign.Center
        )
    }
}
