@file:Suppress("UNREACHABLE_CODE")

package com.a0100019.mypat.presentation.main


import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun TestScreen(
    viewModel: TestViewModel = hiltViewModel(),
) {
    val state : TestState = viewModel.collectAsState().value

    //나중에 사이드 이펙트 쓸때 씀
    val context = LocalContext.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TestSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
//            LoginSideEffect.NavigateToMainActivity -> {
//                context.startActivity(
//                    Intent(
//                        context, MainActivity::class.java
//                    ).apply {
//                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                    }
//                )
//            }
        }
    }

    TestScreen(
        firstNumber = state.firstNumber,
        secondNumber = state.secondNumber,
        onFirstNumberChange = viewModel::onFirstNumberChange,
        onSecondNumberChange = viewModel::onSecondNumberChange,
        onButtonClick = viewModel::onCombineNumbers,
        result = state.result,
        operation = state.operation,
        onOperationChange = viewModel::onOperationChange
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    firstNumber: String,
    secondNumber: String,
    onFirstNumberChange: (String) -> Unit,
    onSecondNumberChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    result: String,
    operation: String,
    onOperationChange: (String) -> Unit
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
                    text = "계산기",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "숫자와 기호를 넣어주세요",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Column (
                modifier = Modifier
                    .padding(top = 24.dp, start = 10.dp, end = 10.dp)
                    .fillMaxHeight()
            ){
                Text(
                    text = "첫번 째 숫자",
                    style = MaterialTheme.typography.headlineMedium
                )
                TextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    value = firstNumber,
                    onValueChange = onFirstNumberChange,
                    visualTransformation = VisualTransformation.None,
                    shape = RoundedCornerShape(8.dp)
                )
                Text(
                    text = "두번 째 숫자",
                    style = MaterialTheme.typography.headlineMedium
                )
                TextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    value = secondNumber,
                    onValueChange = onSecondNumberChange,
                    visualTransformation = VisualTransformation.None,
                    shape = RoundedCornerShape(8.dp)
                )
                Text(
                    text = "기호",
                    style = MaterialTheme.typography.headlineMedium
                )
                TextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    value = operation,
                    onValueChange = onOperationChange,
                    visualTransformation = VisualTransformation.None,
                    shape = RoundedCornerShape(8.dp)
                )
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    onClick = onButtonClick
                ) {
                    Text("계산하기")
                }
                Text(
                    text = result,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TestScreenPreview() {
    MypatTheme {
        TestScreen(
            firstNumber = "11",
            secondNumber = "22",
            onFirstNumberChange = { },
            onSecondNumberChange = { },
            onButtonClick = { },
            result = "100",
            operation = "+",
            onOperationChange = { }
        )
    }
}