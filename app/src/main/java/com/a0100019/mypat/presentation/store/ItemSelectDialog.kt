package com.a0100019.mypat.presentation.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun ItemSelectDialog(
    onCloseClick: () -> Unit,
    onSelectClick: () -> Unit,
    itemData: String,
) {
    Dialog(
        onDismissRequest = onCloseClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "선택하시겠습니까?",
                    style = MaterialTheme.typography.headlineMedium
                )

                val part = itemData.split("@")

                Box {
                    JustImage(
                        filePath = part[0],
                        modifier = Modifier.size(200.dp)
                    )
                }

                Text(
                    text = part[1],
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                ) {
                    MainButton(
                        text = " 취소 ",
                        onClick = onCloseClick,
                        modifier = Modifier
                            .padding(top = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))

                    MainButton(
                        text = " 선택 ",
                        onClick = onSelectClick,
                        modifier = Modifier
                            .padding(top = 16.dp)
                    )

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ItemSelectDialogPreview() {
    MypatTheme {
        ItemSelectDialog(
            onCloseClick = {},
            onSelectClick = {},
            itemData = "pat/cat.json@고양이"
        )
    }
}