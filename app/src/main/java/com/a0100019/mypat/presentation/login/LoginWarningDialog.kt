package com.a0100019.mypat.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun LoginWarningDialog(
    onClose: () -> Unit,
    onConfirmClick: () -> Unit,
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
//                .fillMaxHeight(0.8f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Text(
                    text = "경고",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                    ,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "정상적으로 로그아웃 하지 않은 계정입니다. 하루마을은 데이터 관리를 위해 하나의 기기에서 로그인 해야하며, 여러 기기에서 로그인 시 데이터 손실이 발생합니다. 또한 반복적으로 발생할 경우 버그로 간주하여 제제를 받습니다. 그래도 로그인 하겠습니까?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    MainButton(
                        text = "무시하고 로그인하기",
                        onClick = onConfirmClick,
                        modifier = Modifier
                    )

                    MainButton(
                        text = "아니오",
                        onClick = onClose,
                        modifier = Modifier
                    )

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginWarningDialogPreview() {
    MypatTheme {
        LoginWarningDialog(
            onClose = {},
            onConfirmClick = {},
        )
    }
}