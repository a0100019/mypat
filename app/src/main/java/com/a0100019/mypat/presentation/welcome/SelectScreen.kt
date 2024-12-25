package com.a0100019.mypat.presentation.welcome


import android.content.Intent
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.MainActivity
import com.a0100019.mypat.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun SelectScreen(
    viewModel: SelectViewModel = hiltViewModel(),
) {


//    val state : SelectState = viewModel.collectAsState().value

    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SelectSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            SelectSideEffect.NavigateToMainActivity -> {
                context.startActivity(
                    Intent(
                        context, MainActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
        }
    }


    SelectScreen(
        onCalculatorClick = viewModel::onCalculatorClick
    )

}

@Composable
fun SelectScreen(
    onCalculatorClick: () -> Unit
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
                    onClick = {onCalculatorClick()}
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
                // 여기서 추가적인 UI 요소를 배치할 수 있습니다
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
                        onClick = {}
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
                        onClick = {}
                    ) {
                        Text("상점")
                    }
                    Button(
                        modifier = Modifier,
                        onClick = {}
                    ) {
                        Text("게임")
                    }
                    Button(
                        modifier = Modifier,
                        onClick = {}
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
        SelectScreen(
            onCalculatorClick = { }
        )
    }
}