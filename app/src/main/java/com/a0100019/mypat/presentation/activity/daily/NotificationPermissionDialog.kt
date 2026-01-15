package com.a0100019.mypat.presentation.activity.daily

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun NotificationPermissionDialog(
    onCloseClick: () -> Unit = {},
    onCheckClick: (Context) -> Unit = {},
    situation: String = ""
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onCloseClick
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .padding(16.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .background(Color(0xFFFDF7FF), shape = RoundedCornerShape(20.dp)) // 부드러운 배경
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "알림 권한 요청",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "일일 걸음 수를 실시간으로 가져오기 위해 알림 권한이 필요합니다. 상태 창에서 걸음 수를 확인할 수 있습니다.\n" +
                            "불필요한 알림은 없으니 걱정하지 마세요!",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                )

                Text(
                    text = "1. 권한 선택\n2. 알림 선택\n3. 허용 선택",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                )

                Button(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                    ,
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(intent)
                    }) {
                    Text("설정으로 이동")
                }

                if (situation == "walkPermissionRequestNo") {
                    Text(
                        text = "권한을 허용해주세요"
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MainButton(
                        text = " 취소 ",
                        onClick = onCloseClick
                    )

                    MainButton(
                        text = " 확인 ",
                        onClick = { onCheckClick(context) }
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationPermissionDialogPreview() {
    MypatTheme {
        NotificationPermissionDialog(
        )
    }
}