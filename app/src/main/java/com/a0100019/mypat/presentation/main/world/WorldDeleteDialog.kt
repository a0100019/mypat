package com.a0100019.mypat.presentation.main.world


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

@Composable
fun WorldDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "삭제하시겠습니까?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "네")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "아니오")
            }
        }
    )
}
