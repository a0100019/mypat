package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DiaryFinishDialog(
    onClose: () -> Unit,
) {

    Dialog(
        onDismissRequest = onClose
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
                .padding(32.dp)
        ) {

            Column(
                modifier = Modifier
                        ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "하루 메모 작성을 완료했습니다",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer( modifier = Modifier.size(30.dp))

                MainButton(
                    text = " 닫기 ",
                    onClick = onClose,
                    modifier = Modifier
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryFinishDialogPreview() {
    MypatTheme {
        DiaryFinishDialog(
            onClose = {},
        )
    }
}