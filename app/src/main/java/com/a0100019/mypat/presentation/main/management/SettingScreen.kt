package com.a0100019.mypat.presentation.main.management

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SettingScreen(
    settingViewModel: SettingViewModel = hiltViewModel()

) {

    val settingState : SettingState = settingViewModel.collectAsState().value

    val context = LocalContext.current

    settingViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SettingSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    SettingScreen(
        onClose = settingViewModel::onCloseClick,
        userData = settingState.userData
    )
}

@Composable
fun SettingScreen(
    onClose: () -> Unit,
    userData: List<User>,
) {

    LazyColumn {
        item {
            Button(
                onClick = {}
            ) {
                Text(
                    text = "편지 모음",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
//            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        }
        item {
            Button(
                onClick = {}
            ) {
                Text(
                    text = "로그아웃",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        item {
            Button(
                onClick = {}
            ) {
                Text(
                    text = "이용 약관",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        item {
            Button(
                onClick = {}
            ) {
                Text(
                    text = "계정 삭제",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        item {
            Button(
                onClick = {}
            ) {
                Text(
                    text = "버그 신고",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        item {
            Button(
                onClick = {}
            ) {
                Text(
                    text = "쿠폰 코드",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        item {
            Button(
                onClick = {}
            ) {
                Text(
                    text = "쿠폰 코드",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }



    }


}



@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    MypatTheme {
        SettingScreen(
            onClose = {},
            userData = emptyList()
        )
    }
}