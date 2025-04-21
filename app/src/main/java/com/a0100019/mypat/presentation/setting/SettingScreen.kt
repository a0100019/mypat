package com.a0100019.mypat.presentation.setting

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import com.a0100019.mypat.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun SettingScreen(
    settingViewModel: SettingViewModel = hiltViewModel(),
    onSignOutClick: () -> Unit
) {
    val settingState: SettingState = settingViewModel.collectAsState().value
    val context = LocalContext.current

    settingViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SettingSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            SettingSideEffect.NavigateToLoginScreen -> onSignOutClick()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                settingViewModel.onGoogleLoginClick(idToken)
            } else {
                Log.e("login", "로그인 스크린 로그인 성공")
//                LoginSideEffect.Toast(postLoginSideEffect.Toast("구글 로그인 실패: 토큰 없음"))
            }
        } catch (e: Exception) {
            Log.e("login", "로그인 스크린 로그인 실패: ${e.localizedMessage}", e)
//            loginViewModel.postSideEffect(LoginSideEffect.Toast("구글 로그인 실패"))
        }
    }

    SettingScreen(
        userData = settingState.userDataList,
        googleLoginState = settingState.googleLoginState,
        settingSituation = settingState.settingSituation,
        imageUrl = settingState.imageUrl,

        onClose = settingViewModel::onCloseClick,
        onTermsClick = settingViewModel::onTermsClick,
        onSignOutClick = settingViewModel::dataSave,
        onSituationChange = settingViewModel::onSituationChange,

        onGoogleLoginClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        },

    )
}
@Composable
fun SettingScreen(
    userData: List<User>,
    googleLoginState: Boolean,
    settingSituation: String,
    imageUrl: String,

    onTermsClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onClose: () -> Unit,
    onSituationChange: (String) -> Unit,
    onGoogleLoginClick: () -> Unit,
) {

    when(settingSituation) {
        "terms" -> TermsDialog(
            onClose = onClose,
            imageUrl = imageUrl
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!googleLoginState) {
            item {
                SettingButton(
                    text = "구글 로그인 하기",
                    onClick = onGoogleLoginClick
                )
            }
        }

        item {
            SettingButton(
                text = "편지 모음",
                onClick = { /* TODO */ }
            )
        }

        item {
            SettingButton(
                text = "이용 약관",
                onClick = {
                    onTermsClick()
                    onSituationChange("terms")
                }
            )
        }

        item {
            SettingButton(
                text = "계정 삭제",
                onClick = { /* TODO */ }
            )
        }

        item {
            SettingButton(
                text = "버그 신고",
                onClick = { /* TODO */ }
            )
        }

        item {
            SettingButton(
                text = "쿠폰 코드",
                onClick = { /* TODO */ }
            )
        }

        if (googleLoginState) {
            item {
                SettingButton(
                    text = "로그아웃",
                    onClick = onSignOutClick
                )
            }
        }
    }
}

@Composable
fun SettingButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF0F0F0),
            contentColor = Color.Black
        )
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(vertical = 12.dp)
        )
    }
}



@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    MypatTheme {
        SettingScreen(
            onClose = {},
            userData = emptyList(),
            onTermsClick = {},
            onSignOutClick = {},
            googleLoginState = false,
            onGoogleLoginClick = {},
            settingSituation = "",
            onSituationChange = {},
            imageUrl = ""
        )
    }
}