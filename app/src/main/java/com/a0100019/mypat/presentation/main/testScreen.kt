package com.a0100019.mypat.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.a0100019.mypat.presentation.main.management.MainNavHost

@Composable
fun FullScreenWithContent() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray) // 초과 여백 색상
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val minAspectRatio = 9 / 22f // 최소 비율
        val maxAspectRatio = 9 / 14f // 최대 비율
        val actualAspectRatio = screenWidth / screenHeight

        val contentWidth: Dp
        val contentHeight: Dp

        if (actualAspectRatio in minAspectRatio..maxAspectRatio) {
            // 비율이 범위 내에 있는 경우
            val targetAspectRatio = actualAspectRatio // 현재 비율 그대로 사용
            if (actualAspectRatio > maxAspectRatio) {
                contentHeight = screenHeight
                contentWidth = screenHeight * targetAspectRatio
            } else {
                contentWidth = screenWidth
                contentHeight = screenWidth / targetAspectRatio
            }
        } else {
            // 비율이 범위를 벗어난 경우: 여백 추가
            if (actualAspectRatio > maxAspectRatio) {
                // 가로가 길어서 여백이 생기는 경우
                contentHeight = screenHeight
                contentWidth = screenHeight * maxAspectRatio
            } else {
                // 세로가 길어서 여백이 생기는 경우
                contentWidth = screenWidth
                contentHeight = screenWidth / minAspectRatio
            }
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
