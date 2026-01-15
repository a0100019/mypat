package com.a0100019.mypat.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun LoginLoadingDialog() {

    Dialog(
        onDismissRequest = { }
    ) {
        Box(
            modifier = Modifier
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 32.dp, vertical = 28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                JustImage(
                    filePath = "etc/loadingCat.json",
                    modifier = Modifier
                        .size(150.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 💬 안내 문구
                Text(
                    text = "로딩 중...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginLoadingDialogPreview() {
    MypatTheme {
        LoginLoadingDialog(
        )
    }
}