package com.a0100019.mypat.presentation.operator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
fun OperatorChatDialog(
    onClose: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
    onTextChange2: (String) -> Unit = {},
    onTextChange3: (String) -> Unit = {},
    text: String = "",
    text2: String = "",
    text3: String = "",
    onOperatorChatSubmitClick: () -> Unit = {}

) {

    Dialog(
        onDismissRequest = onClose
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
                    text = "채팅 작성",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(10.dp),
                )

                OutlinedTextField(
                    value = text2,
                    onValueChange = onTextChange2,
                    label = { Text("이름") },
                    placeholder = { Text(
                        textAlign = TextAlign.Center,
                        text = "이름") },
                    shape = RoundedCornerShape(8.dp), // 테두리를 둥글게
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                OutlinedTextField(
                    value = text3,
                    onValueChange = onTextChange3,
                    label = { Text("태그") },
                    placeholder = { Text(
                        textAlign = TextAlign.Center,
                        text = "태그") },
                    shape = RoundedCornerShape(8.dp), // 테두리를 둥글게
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text("내용") },
                    placeholder = { Text(
                        textAlign = TextAlign.Center,
                        text = "내용") },
                    shape = RoundedCornerShape(8.dp), // 테두리를 둥글게
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .padding(8.dp)
                )

                // 추가로 원하는 Composable 요소

                Row {
                    MainButton(
                        text = " 취소 ",
                        onClick = onClose,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    MainButton(
                        text = " 전송 ",
                        onClick = onOperatorChatSubmitClick,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OperatorChatDialogPreview() {
    MypatTheme {
        OperatorChatDialog(
            onClose = {},
            onTextChange = {},
            text = "",
        )
    }
}