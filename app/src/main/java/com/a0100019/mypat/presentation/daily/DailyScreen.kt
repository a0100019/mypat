package com.a0100019.mypat.presentation.daily

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.image.DisplayKoreanIdiomImage
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.ui.theme.MypatTheme

@Composable
fun DailyScreen(
    viewModel: LoadingViewModel = hiltViewModel(),
    onWalkNavigateClick: () -> Unit,
    onDiaryNavigateClick: () -> Unit,
    onEnglishNavigateClick: () -> Unit,
    onKoreanIdiomNavigateClick: () -> Unit,
) {

    DailyScreen(
        onWalkNavigateClick = onWalkNavigateClick,
        onDiaryNavigateClick = onDiaryNavigateClick,
        onEnglishNavigateClick = onEnglishNavigateClick,
        onKoreanIdiomNavigateClick = onKoreanIdiomNavigateClick,
        value = ""
    )
    
}



@Composable
fun DailyScreen(
    onWalkNavigateClick: () -> Unit,
    onDiaryNavigateClick: () -> Unit,
    onEnglishNavigateClick: () -> Unit,
    onKoreanIdiomNavigateClick: () -> Unit,
    value : String
) {
    Surface {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Column (
                modifier = Modifier.padding(top = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Daily",
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Column (
                modifier = Modifier
                    .padding(top = 24.dp, start = 10.dp, end = 10.dp)
                    .fillMaxHeight()
            ){
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    onClick = onWalkNavigateClick
                ) {
                    Text("만보기")
                }
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    onClick = onDiaryNavigateClick
                ) {
                    Text("일기")
                }
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    onClick = onEnglishNavigateClick
                ) {
                    Text("영단어")
                }
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    onClick = onKoreanIdiomNavigateClick
                ) {
                    Text("사자성어")
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyScreenPreview() {
    MypatTheme {
        DailyScreen(
            onWalkNavigateClick = {  },
            onDiaryNavigateClick = {  },
            onEnglishNavigateClick = {  },
            onKoreanIdiomNavigateClick = {  },
            value = ""
        )
    }
}