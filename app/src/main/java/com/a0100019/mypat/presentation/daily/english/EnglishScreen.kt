package com.a0100019.mypat.presentation.daily.english

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun EnglishScreen(
    viewModel: LoadingViewModel = hiltViewModel()

) {

    EnglishScreen(
        value = "스크린 나누기"
    )
}



@Composable
fun EnglishScreen(
    value : String
) {
    // Fullscreen container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Optional: Set background color
        contentAlignment = Alignment.Center // Center content
    ) {
        // Text in the center
        Text(
            text = "EnglishScreen",
            fontSize = 32.sp, // Large font size
            fontWeight = FontWeight.Bold, // Bold text
            color = Color.Black // Text color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EnglishScreenPreview() {
    MypatTheme {
        EnglishScreen(
            value = ""
        )
    }
}