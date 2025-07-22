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
import com.a0100019.mypat.presentation.ui.component.MainButton
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

    SettingScreen(

        userData = settingState.userDataList,
        settingSituation = settingState.settingSituation,
        imageUrl = settingState.imageUrl,
        editText = settingState.editText,
        clickLetterData = settingState.clickLetterData,
        letterDataList = settingState.letterDataList,

        onClose = settingViewModel::onCloseClick,
        onSignOutClick = settingViewModel::dataSave,
        onSituationChange = settingViewModel::onSituationChange,
        onAccountDeleteClick = settingViewModel::onAccountDeleteClick,
        onEditTextChange = settingViewModel::onEditTextChange,
        onCouponConfirmClick = settingViewModel::onCouponConfirmClick,
        onSettingTalkConfirmClick = settingViewModel::onSettingTalkConfirmClick,
        clickLetterDataChange = settingViewModel::clickLetterDataChange,
        onLetterConfirmClick = settingViewModel::onLetterConfirmClick,
        onLetterLinkClick = settingViewModel::onLetterLinkClick,
        onLetterCloseClick = settingViewModel::onLetterCloseClick
    )
}

@Composable
fun SettingScreen(

    userData: List<User>,
    settingSituation: String,
    imageUrl: String,
    editText: String,
    letterDataList: List<Letter>,
    clickLetterData: Letter,

    onSignOutClick: () -> Unit,
    onClose: () -> Unit,
    onSituationChange: (String) -> Unit,
    onAccountDeleteClick: () -> Unit,
    onEditTextChange: (String) -> Unit,
    onCouponConfirmClick: () -> Unit,
    onSettingTalkConfirmClick: () -> Unit,
    clickLetterDataChange: (Int) -> Unit,
    onLetterLinkClick: () -> Unit,
    onLetterConfirmClick: () -> Unit = {},
    onLetterCloseClick: () -> Unit = {}

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
            onLetterLinkClick = onLetterLinkClick,
            onLetterConfirmClick = onLetterConfirmClick
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            MainButton(
                text = "편지 모음",
                onClick = {
                    onSituationChange("letter")
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        item {
            MainButton(
                text = "이용 약관",
                onClick = {
                    onSituationChange("terms")
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        item {
            MainButton(
                text = "계정 삭제",
                onClick = {
                    onSituationChange("accountDelete")
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        item {
            MainButton(
                text = "버그 신고",
                onClick = {
                    onSituationChange("settingTalk")
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        item {
            MainButton(
                text = "쿠폰 코드",
                onClick = {
                    onSituationChange("coupon")
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        item {
            MainButton(
                text = "로그아웃",
                onClick = onSignOutClick,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    MypatTheme {
        SettingScreen(
            onClose = {},
            userData = emptyList(),
            onSignOutClick = {},
            settingSituation = "",
            onSituationChange = {},
            onAccountDeleteClick = {},
            onEditTextChange = {},
            editText = "",
            onCouponConfirmClick = {},
            onSettingTalkConfirmClick = {},
            clickLetterData = Letter(),
            clickLetterDataChange = {},
            letterDataList = emptyList(),
            onLetterLinkClick = {},
            onLetterCloseClick = {},
            onLetterConfirmClick = {},
            imageUrl = ""


        )
    }
}