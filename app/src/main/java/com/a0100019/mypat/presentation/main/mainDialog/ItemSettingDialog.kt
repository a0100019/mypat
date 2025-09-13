package com.a0100019.mypat.presentation.main.mainDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun ItemSettingDialog(
    onDelete: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onSizeUp: () -> Unit = {},
    onSizeDown: () -> Unit = {},
    itemData: Item = Item(url = "", name = "이름")
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
//                .fillMaxHeight(0.5f)
                .shadow(12.dp, shape = RoundedCornerShape(24.dp))
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
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
                    text = itemData.name,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                    ,
                    style = MaterialTheme.typography.headlineMedium
                    )

                val sizeRatio = when (itemData.sizeFloat/itemData.minFloat) {
                    in 0.9..1.1 -> "1"
                    in 1.15..1.35 -> "1.25"
                    in 1.4..1.6 -> "1.5"
                    in 1.65..1.85 -> "1.75"
                    in 1.9..2.1 -> "2"
                    else -> "??"
                }
                Text(
                    text = "현재 크기 ： ｘ$sizeRatio",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                    ,
                ) {
                    MainButton(
                        text = " 지우기 ",
                        onClick = onDelete,
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    MainButton(
                        text = " 줄이기 ",
                        onClick = onSizeDown,
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.size(6.dp))
                    
                    MainButton(
                        text = " 키우기 ",
                        onClick = onSizeUp,
                        modifier = Modifier
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingTalkDialogPreview() {
    MypatTheme {
        ItemSettingDialog(
        )
    }
}