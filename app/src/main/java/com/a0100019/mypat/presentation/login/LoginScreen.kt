@file:Suppress("LABEL_NAME_CLASH")

package com.a0100019.mypat.presentation.login

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.a0100019.mypat.R
import com.a0100019.mypat.presentation.main.management.MainRoute
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.border
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import com.a0100019.mypat.presentation.setting.TermsDialog
import com.a0100019.mypat.presentation.ui.MusicPlayer

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
        loginState = loginState.loginState,
        dialog = loginState.dialog,

        onNavigateToMainScreen = loginViewModel::onNavigateToMainScreen,
        dialogChange = loginViewModel::dialogChange,

        googleLoginClick = {
            if (!isInternetAvailable(context)) {
                Toast.makeText(context, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
                return@LoginScreen
            }

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)

            // 🔥 자동 로그인 방지: 로그아웃 후 다시 실행
            googleSignInClient.signOut().addOnCompleteListener {
                launcher.launch(googleSignInClient.signInIntent)
            }
        }
        ,
    )
}

//인터넷 연결 확인 코드
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun LoginScreen(
    googleLoginClick: () -> Unit,
    onNavigateToMainScreen: () -> Unit,
    dialogChange: (String) -> Unit = {},

    loginState: String,
    dialog: String = ""
) {

//    MusicPlayer(
//        R.raw.bmg1
//    )

    // 상태를 remember로 관리해야 UI가 갱신됨
    var termsChecked by remember { mutableStateOf(false) }
    var privacyChecked by remember { mutableStateOf(false) }

    if(dialog == "loginWarning") {
        LoginWarningDialog(
            onClose = { dialogChange("") },
            onConfirmClick = { dialogChange("check") }
        )
    } else if(dialog == "terms") {
        TermsDialog(
            onClose = { dialogChange("") }
        )
    }

    Box {

        JustImage(
            filePath = "etc/loginScreen.png",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        when (loginState) {
            "unLogin" -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                    ,
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 2.dp,
                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f) // 50% 투명
                ) {

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Text(
                            text = "마을 친구들을 만나려면 작은 약속이 필요해요",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column {
                                Text(
                                    text = "이용약관에 동의합니다.",
                                )
                            }

                            Checkbox(
                                checked = termsChecked,
                                onCheckedChange = { termsChecked = it }
                            )

                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "개인정보 처리방침에 동의합니다.",
                            )
                            Checkbox(
                                checked = privacyChecked,
                                onCheckedChange = { privacyChecked = it }
                            )

                        }

                        Text(
                            text = "이용약관 및 개인정보 처리방침 보기",
                            modifier = Modifier.clickable {
                                dialogChange("terms")
                            },
                            color = Color.Black
                        )


                    }

                }

                Spacer(modifier = Modifier.size(20.dp))

                // ✅ 구글 로그인 버튼
                Button(
                    onClick = {
                        if(termsChecked && privacyChecked) { googleLoginClick() }
                              },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .alpha(if(termsChecked && privacyChecked) 1f else 0.7f) // 🔹 전체 투명도 (70% 불투명)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        JustImage(
                            filePath = "etc/googleLogo.png",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("구글 로그인")
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                Text(
                    text = "하루마을에 오신 것을 환영합니다!",
                )

            }

            "login" -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onNavigateToMainScreen()
                    },
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "화면 터치해서 시작",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.size(70.dp))

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
            onNavigateToMainScreen = {},
            loginState = "unLogin"
        )
    }
}