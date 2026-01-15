package com.a0100019.mypat.presentation.neighbor.privateChat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun PrivateRoomCreateDialog(
    onClose: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
    yourTag: String = "",
    onConfirmClick: () -> Unit = {},
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
//                .fillMaxHeight(0.8f)
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
                    text = "친구 찾기",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp),
                )

                Text(text = "친구하고 싶은 이웃의 태그를 입력해주세요")

                OutlinedTextField(
                    value = yourTag,
                    onValueChange = { input ->
                        // 숫자만 허용 + 최대 10자리
                        if (input.all { it.isDigit() } && input.length <= 10) {
                            onTextChange(input)
                        }
                    },
                    label = { Text("태그") },
                    placeholder = { Text("상대방의 태그를 입력하세요.") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )


                Spacer(modifier = Modifier.height(16.dp))


                // 추가로 원하는 Composable 요소

                Row {
                    MainButton(
                        text = " 취소 ",
                        onClick = onClose,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    MainButton(
                        text = " 확인 ",
                        onClick = onConfirmClick,
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
fun PrivateRoomCreateDialogPreview() {
    MypatTheme {
        PrivateRoomCreateDialog(
            onClose = {},
            onConfirmClick = {},
        )
    }
}