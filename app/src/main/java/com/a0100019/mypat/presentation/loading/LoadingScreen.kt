package com.a0100019.mypat.presentation.loading

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.image.loadBitmapFromAssets
import com.a0100019.mypat.presentation.image.DisplayKoreanIdiomImage
import com.a0100019.mypat.ui.theme.MypatTheme


@Composable
fun LoadingScreen(
    viewModel: LoadingViewModel = hiltViewModel()

) {

    LoadingScreen(
        value = "스크린 나누기"
    )
}



@Composable
fun LoadingScreen(
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
            text = "로딩 중",
            fontSize = 32.sp, // Large font size
            fontWeight = FontWeight.Bold, // Bold text
            color = Color.Black // Text color
        )
        DisplayKoreanIdiomImage("koreanIdiomImage/jukmagow1.jpg")
    }
}

@Preview(showBackground = true)
@Composable
fun SelectScreenPreview() {
    MypatTheme {
        LoadingScreen(
            value = ""
        )
    }
}