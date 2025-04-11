package com.a0100019.mypat.presentation.login

import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.a0100019.mypat.R
import com.a0100019.mypat.presentation.loading.LoadingSideEffect
import com.a0100019.mypat.presentation.loading.LoadingState
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.presentation.main.management.MainRoute
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navController: NavController // NavController를 파라미터로 받아오기
) {
    val context = LocalContext.current

    // 🔥 여기서 네비게이션 처리
    loginViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            is LoginSideEffect.NavigateToMainScreen -> {
                navController.navigate(MainRoute.MainScreen.name) {
                    popUpTo(MainRoute.LoginScreen.name) { inclusive = true } // 뒤로가기 방지
                }
            }
        }
    }

    val loginState = loginViewModel.collectAsState().value

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                loginViewModel.onGoogleLoginClick(idToken)
            } else {
                Log.e("login", "로그인 스크린 로그인 성공")
//                LoginSideEffect.Toast(postLoginSideEffect.Toast("구글 로그인 실패: 토큰 없음"))
            }
        } catch (e: Exception) {
            Log.e("login", "로그인 스크린 로그인 실패: ${e.localizedMessage}", e)
//            loginViewModel.postSideEffect(LoginSideEffect.Toast("구글 로그인 실패"))
        }
    }


    LoginScreen(
        googleLoginClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        },
        guestLoginClick = loginViewModel::onGuestLoginClick
    )
}

@Composable
fun LoginScreen(
    guestLoginClick: () -> Unit,
    googleLoginClick: () -> Unit,
) {
    Column {
        Text("로그인")
        Button(
            onClick = {}
        ) {
            Text("게스트 로그인")
        }

        Button(
            onClick = googleLoginClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier
                .height(48.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                JustImage(
                    filePath = "etc/googleLogo.png",
                    modifier = Modifier
                        .size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("구글 로그인")
            }
        }



    }


}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MypatTheme {
        LoginScreen(
            googleLoginClick = {},
            guestLoginClick = {}
        )
    }
}