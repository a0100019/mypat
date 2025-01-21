package com.a0100019.mypat.presentation.ui.mainDialog

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.a0100019.mypat.data.room.item.Item

@Composable
fun ItemSettingDialog(
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    onSizeUp: () -> Unit,
    onSizeDown: () -> Unit,
    itemData: Item
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = itemData.name)
        },
        text = {
            Text("${itemData.name}에 대한 크기 조정 및 삭제를 수행할 수 있습니다.\n 현재 크기 : ${itemData.sizeFloat.toDouble()}")
        },
        confirmButton = {
            TextButton(onClick = { onDelete() }) {
                Text("삭제하기")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { onSizeDown() }) {
                    Text("줄이기")
                }
                TextButton(onClick = { onSizeUp() }) {
                    Text("키우기")
                }
            }
        }
    )
}
