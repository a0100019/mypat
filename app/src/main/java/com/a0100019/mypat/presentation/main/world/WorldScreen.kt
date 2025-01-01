package com.a0100019.mypat.presentation.main.world

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.image.DisplayKoreanIdiomImage
import com.a0100019.mypat.presentation.image.DisplayMapImage
import com.a0100019.mypat.presentation.image.LottieCatAnimation
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.ui.theme.MypatTheme

@Composable
fun WorldScreen(
    viewModel: WorldViewModel = hiltViewModel()

) {

    WorldScreen(
        value = "스크린 나누기"
    )
}



@Composable
fun WorldScreen(
    value : String
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth() // 가로 크기는 최대
            .aspectRatio(1 / 1.25f) // 세로가 가로의 1.25배
            .padding(10.dp), // padding 추가
        color = Color.Gray
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // Optional: Set background color
            contentAlignment = Alignment.Center // Center content
        ) {
            DisplayMapImage("map/beach.jpg")
            // Text in the center
//            Text(
//                text = "로딩 중",
//                fontSize = 32.sp, // Large font size
//                fontWeight = FontWeight.Bold, // Bold text
//                color = Color.Black // Text color
//            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(200.dp))
                LottieCatAnimation()
                Text(text ="지아코", color = Color.Black)
            }

        }
        Text("aa")


    }

    // Fullscreen container

}

@Preview(showBackground = true)
@Composable
fun SelectScreenPreview() {
    MypatTheme {
        WorldScreen(
            value = ""
        )
    }
}