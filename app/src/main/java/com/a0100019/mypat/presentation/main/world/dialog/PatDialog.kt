package com.a0100019.mypat.presentation.main.world.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun MainScreen1() {
    var isDialogVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 콘텐츠
        Button(
            onClick = { isDialogVisible = true },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Open Dialog with Screen")
        }

        // 다이얼로그 표시
        if (isDialogVisible) {
            Dialog(
                onDismissRequest = { isDialogVisible = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f) // 화면의 60%만 차지
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        // 다이얼로그 안의 Screen
                        DialogScreenContent(
                            onClose = { isDialogVisible = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DialogScreenContent(
    onClose: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "1",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onClose,
            modifier = Modifier.align(Alignment.End).padding(16.dp)
        ) {
            Text("Close")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 추가로 원하는 Composable 요소
        Text("Add any other content here.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* 다른 동작 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Perform Action")
        }
    }
}
