package com.a0100019.mypat.presentation.login

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.loading.LoadingSideEffect
import com.a0100019.mypat.presentation.loading.LoadingState
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel()

) {

    val loginState : LoginState = loginViewModel.collectAsState().value

    val context = LocalContext.current

    loginViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    LoginScreen(
        value = "스크린 나누기"
    )
}

@Composable
fun LoginScreen(
    value : String
) {
    Column {
        Text("로그인")
        Button(
            onClick = {}
        ) {
            Text("게스트 로그인")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MypatTheme {
        LoginScreen(
            value = ""
        )
    }
}