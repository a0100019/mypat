package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.ui.theme.MypatTheme

@Composable
fun DiaryScreen(
    viewModel: LoadingViewModel = hiltViewModel()

) {

    DiaryScreen(
        value = "스크린 나누기"
    )
}



@Composable
fun DiaryScreen(
    value : String
) {
    // Fullscreen container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Optional: Set background color
    ) {
        // Text in the center
        Text(
            text = "안녕",
            fontSize = 32.sp, // Large font size
            fontWeight = FontWeight.Bold, // Bold text
            color = Color.Black // Text color
        )
        Button(
            onClick = {}
        ) {
            Text("바보")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryScreenPreview() {
    MypatTheme {
        DiaryScreen(
            value = ""
        )
    }
}