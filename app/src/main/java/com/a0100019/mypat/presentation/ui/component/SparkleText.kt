package com.a0100019.mypat.presentation.ui.component

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SparkleText(
    text: String = "text",
    fontSize: Int = 14,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Text(
        text = text,
        color = MaterialTheme.colorScheme.onErrorContainer,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .alpha(alpha)
            .background(MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp)) // 파스텔 블루 배경
            .padding(horizontal = 10.dp, vertical = 4.dp)
                ,
        fontSize = fontSize.sp
    )
}

@Preview(showBackground = true, name = "Sparkle Text Preview")
@Composable
fun PreviewSparkleText() {
    Surface(color = Color(0xFFF8F9FA)) { // 부드러운 배경 추가
        SparkleText(
            text = "오늘도 힘내요! ✨",
            fontSize = 18,
            modifier = Modifier.padding(16.dp)
        )
    }
}
