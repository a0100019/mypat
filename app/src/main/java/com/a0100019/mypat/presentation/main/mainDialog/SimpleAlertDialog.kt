package com.a0100019.mypat.presentation.main.mainDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun SimpleAlertDialog(
    onConfirmClick: () -> Unit = {},
    onDismissClick: () -> Unit = {},
    text: String = "이 작업을 수행하시겠습니까?",
    onDismissOn: Boolean = true,
    title: String = "확인"
) {

    Dialog(
        onDismissRequest = onDismissClick
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline, // 테두리
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.background, // 배경색
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.size(32.dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(32.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Spacer(modifier = Modifier.weight(1f))

                    if(onDismissOn) {
                        MainButton(
                            text = " 취소 ",
                            onClick = onDismissClick,
                            modifier = Modifier
                        )
                    }
                    Spacer(modifier = Modifier.size(12.dp))

                    MainButton(
                        text = " 확인 ",
                        onClick = onConfirmClick,
                        modifier = Modifier
                    )

                }

            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun SimpleAlertDialogPreview() {
    MypatTheme {
        SimpleAlertDialog(
            onConfirmClick = {},
            onDismissClick = {},
        )
    }
}
