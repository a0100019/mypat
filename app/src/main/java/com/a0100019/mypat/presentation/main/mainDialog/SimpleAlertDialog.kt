package com.a0100019.mypat.presentation.main.mainDialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun SimpleAlertDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    text: String = "이 작업을 수행하시겠습니까?"
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "확인")
        },
        text = {
            Text(text)
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("예")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("아니요")
            }
        }
    )
}
