package com.a0100019.mypat.presentation.setting

import android.content.Intent
import android.net.Uri
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
import com.a0100019.mypat.data.room.letter.Letter
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
            is SettingSideEffect.OpenUrl -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sideEffect.url))
                context.startActivity(intent)
            }
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
        onGoogleLoginClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)

            // 🔥 자동 로그인 방지: 이전 계정 로그아웃, 로그아웃 시 아이디 선택창 뜸
            googleSignInClient.signOut().addOnCompleteListener {
                launcher.launch(googleSignInClient.signInIntent)
            }
        },

        userData = settingState.userDataList,
        googleLoginState = settingState.googleLoginState,
        settingSituation = settingState.settingSituation,
        imageUrl = settingState.imageUrl,
        editText = settingState.editText,
        clickLetterData = settingState.clickLetterData,
        letterDataList = settingState.letterDataList,
        letterImages = settingState.letterImages,

        onClose = settingViewModel::onCloseClick,
        onTermsClick = settingViewModel::onTermsClick,
        onSignOutClick = settingViewModel::dataSave,
        onSituationChange = settingViewModel::onSituationChange,
        onAccountDeleteClick = settingViewModel::onAccountDeleteClick,
        onEditTextChange = settingViewModel::onEditTextChange,
        onCouponConfirmClick = settingViewModel::onCouponConfirmClick,
        onSettingTalkConfirmClick = settingViewModel::onSettingTalkConfirmClick,
        clickLetterDataChange = settingViewModel::clickLetterDataChange,
        onLetterGetClick = settingViewModel::onLetterGetClick,
        onLetterLinkClick = settingViewModel::onLetterLinkClick,
        onLetterCloseClick = settingViewModel::onLetterCloseClick

    )
}


@Composable
fun SettingScreen(
    userData: List<User>,
    googleLoginState: Boolean,
    settingSituation: String,
    imageUrl: String,
    editText: String,
    letterDataList: List<Letter>,
    clickLetterData: Letter,
    letterImages: List<String>,


    onTermsClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onClose: () -> Unit,
    onSituationChange: (String) -> Unit,
    onGoogleLoginClick: () -> Unit,
    onAccountDeleteClick: () -> Unit,
    onEditTextChange: (String) -> Unit,
    onCouponConfirmClick: () -> Unit,
    onSettingTalkConfirmClick: () -> Unit,
    clickLetterDataChange: (Int) -> Unit,
    onLetterLinkClick: () -> Unit,
    onLetterGetClick: () -> Unit,
    onLetterCloseClick: () -> Unit,

) {

    when(settingSituation) {
        "terms" -> TermsDialog(
            onClose = onClose,
            imageUrl = imageUrl
        )
        "accountDelete" -> AccountDeleteDialog(
            onClose = onClose,
            onAccountDeleteTextChange = onEditTextChange,
            accountDeleteString = editText,
            onConfirmClick = onAccountDeleteClick
        )
        "coupon" -> CouponDialog(
            onClose = onClose,
            onCouponTextChange = onEditTextChange,
            couponText = editText,
            onConfirmClick = onCouponConfirmClick
        )
        "settingTalk" -> SettingTalkDialog(
            onClose = onClose,
            onSettingTalkTextChange = onEditTextChange,
            settingTalkText = editText,
            onConfirmClick = onSettingTalkConfirmClick
        )
        "letter" -> LetterDialog(
            onClose = onClose,
            onLetterClick = clickLetterDataChange,
            letterDataList = letterDataList
        )
    }

    if(clickLetterData.id != 0) {
        LetterViewDialog(
            onClose = onLetterCloseClick,
            clickLetterData = clickLetterData,
            letterImages = letterImages,
            onLetterLinkClick = onLetterLinkClick,
            onLetterGetClick = onLetterGetClick
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
                onClick = {
                    onSituationChange("letter")
                }
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
                onClick = {
                    onSituationChange("accountDelete")
                }
            )
        }

        item {
            SettingButton(
                text = "버그 신고",
                onClick = {
                    onSituationChange("settingTalk")
                }
            )
        }

        item {
            SettingButton(
                text = "쿠폰 코드",
                onClick = {
                    onSituationChange("coupon")
                }
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
            imageUrl = "",
            onAccountDeleteClick = {},
            onEditTextChange = {},
            editText = "",
            onCouponConfirmClick = {},
            onSettingTalkConfirmClick = {},
            clickLetterData = Letter(),
            clickLetterDataChange = {},
            letterDataList = emptyList(),
            letterImages = emptyList(),
            onLetterLinkClick = {},
            onLetterGetClick = {},
            onLetterCloseClick = {}

        )
    }
}