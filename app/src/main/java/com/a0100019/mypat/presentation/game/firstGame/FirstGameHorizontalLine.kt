package com.a0100019.mypat.presentation.game.firstGame

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max

@Composable
fun FirstGameHorizontalLine(
    value: Int,
    maxPower: Int,
    modifier: Modifier = Modifier
) {
    val percentage by animateFloatAsState(
        targetValue = (value.toFloat() / maxPower).coerceIn(0f, 1f),
        label = "powerProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE0E0E0)) // 연회색 배경
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(percentage)
                .background(Color(0xFF42A5F5)) // 파란색 진행 바
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun FirstGameHorizontalLinePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("파워 게이지")
        FirstGameHorizontalLine(
            value = 60,
            maxPower = 100
        )
    }
}
