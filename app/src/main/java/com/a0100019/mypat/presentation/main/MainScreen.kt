package com.a0100019.mypat.presentation.main


import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.image.DisplayKoreanIdiomImage
import com.a0100019.mypat.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onDailyNavigateClick: () -> Unit,
    onStoreNavigateClick: () -> Unit,
    onGameNavigateClick: () -> Unit,
    onIndexNavigateClick: () -> Unit
) {


//    val state : SelectState = viewModel.collectAsState().value

    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MainSideEffect.Toast ->
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()

        }
    }



    MainScreen(
        onDailyNavigateClick = onDailyNavigateClick,
        onGameNavigateClick = onGameNavigateClick,
        onIndexNavigateClick = onIndexNavigateClick,
        onStoreNavigateClick = onStoreNavigateClick,
        test = ""
    )

}

@Composable
fun MainScreen(
    onDailyNavigateClick: () -> Unit,
    onStoreNavigateClick: () -> Unit,
    onGameNavigateClick: () -> Unit,
    onIndexNavigateClick: () -> Unit,
    test: String
) {

    Surface {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {}
                ) {
                    Text("내 정보")
                }
                Button(
                    onClick = {}
                ) {
                    Text("설정")
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth() // 가로 크기는 최대
                    .fillMaxHeight(0.5f)
                    .padding(10.dp), // padding 추가
                color = Color.Gray
            ) {
                DisplayKoreanIdiomImage("koreanIdiomImage/jukmagow1.jpg")

            }

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                        onClick = onDailyNavigateClick
                    ) {
                        Text("일일 루틴")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier,
                        onClick = onStoreNavigateClick
                    ) {
                        Text("상점")
                    }
                    Button(
                        modifier = Modifier,
                        onClick = onGameNavigateClick
                    ) {
                        Text("게임")
                    }
                    Button(
                        modifier = Modifier,
                        onClick = onIndexNavigateClick
                    ) {
                        Text("도감")
                    }
                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectScreenPreview() {
    MypatTheme {
        MainScreen(
            onDailyNavigateClick = {},
            onGameNavigateClick = {},
            onIndexNavigateClick = {},
            onStoreNavigateClick = {},
            test = ""
        )
    }
}