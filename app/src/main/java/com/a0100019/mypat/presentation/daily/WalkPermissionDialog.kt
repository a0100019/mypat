package com.a0100019.mypat.presentation.daily

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import android.provider.Settings
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext

@Composable
fun WalkPermissionDialog(
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
                .fillMaxWidth()
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
                    text = "권한 허용"
                )

                Button(onClick = {
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

                Row {
                    MainButton(
                        text = "취소",
                        onClick = onCloseClick
                    )

                    MainButton(
                        text = "확인",
                        onClick = { onCheckClick(context) }
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalkPermissionDialogPreview() {
    MypatTheme {
        WalkPermissionDialog(
        )
    }
}