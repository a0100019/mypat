package com.a0100019.mypat.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun FullScreenWithContent() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray) // 초과 여백 색상
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val targetAspectRatio = 9 / 16f
        val actualAspectRatio = screenWidth / screenHeight

        val contentWidth: Dp
        val contentHeight: Dp

        if (actualAspectRatio > targetAspectRatio) {
            contentHeight = screenHeight
            contentWidth = screenHeight * targetAspectRatio
        } else {
            contentWidth = screenWidth
            contentHeight = screenWidth / targetAspectRatio
        }

        // 중앙 콘텐츠
        Column(
            modifier = Modifier
                .size(contentWidth, contentHeight)
                .align(Alignment.Center)
                .background(Color.Blue), // 콘텐츠 배경색
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MainNavHost()
        }
    }
}
