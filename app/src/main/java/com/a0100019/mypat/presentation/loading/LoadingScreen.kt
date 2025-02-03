package com.a0100019.mypat.presentation.loading

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun LoadingScreen(
    loadingViewModel: LoadingViewModel = hiltViewModel()

) {

    val loadingState : LoadingState = loadingViewModel.collectAsState().value

    val context = LocalContext.current

    loadingViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoadingSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

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
        KoreanIdiomImage("koreanIdiomImage/jukmagow1.jpg")
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