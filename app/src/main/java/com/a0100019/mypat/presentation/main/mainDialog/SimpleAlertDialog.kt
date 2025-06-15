package com.a0100019.mypat.presentation.main.mainDialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.a0100019.mypat.presentation.store.RoomUpDialog
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun SimpleAlertDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    text: String = "이 작업을 수행하시겠습니까?"
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "확인",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(
                    "예",
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    "아니요",
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SimpleAlertDialogPreview() {
    MypatTheme {
        SimpleAlertDialog(
            onConfirm = {},
            onDismiss = {},
        )
    }
}
