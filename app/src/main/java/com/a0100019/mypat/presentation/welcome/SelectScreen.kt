package com.a0100019.mypat.presentation.welcome


import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
        Column {
            Text("원하는 기능을 선택해주세요")
            Button(
                onClick = { onCalculatorClick() }
            ) {
                Text("계산기")
            }
        }
    }
}

@Preview
@Composable
fun SelectScreenPreview() {
    MypatTheme {
        SelectScreen(
            onCalculatorClick = { }
        )
    }
}