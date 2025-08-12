package com.a0100019.mypat.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun AccountDeleteDialog(
    onClose: () -> Unit,
    onAccountDeleteTextChange: (String) -> Unit,
    accountDeleteString: String,
    onConfirmClick: () -> Unit,
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .fillMaxHeight(0.8f)
                .padding(16.dp)
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
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
            ,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {

                Text(
                    text = "계정을 삭제하시겠습니까?",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(13.dp),
                )

                Text(
                    text = "펫들이 슬퍼하고 있어요 다시 한번 생각해주세요... 모든 데이터가 삭제됩니다. 불편한 점이 있었다면 대나무 숲에 작성해주세요 반영하도록 노력하겠습니다",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(7.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = accountDeleteString,
                    onValueChange = onAccountDeleteTextChange,
                    label = { Text("계정삭제") },
                    placeholder = { Text("[계정삭제]라고 입력해주세요.") },
                    singleLine = true,
                    isError = true,
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = Color.Blue,
//                unfocusedBorderColor = Color.Gray
//            ),
                    shape = RoundedCornerShape(8.dp), // 테두리를 둥글게
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )


                Spacer(modifier = Modifier.height(16.dp))

                // 추가로 원하는 Composable 요소

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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
fun AccountDeleteDialogPreview() {
    MypatTheme {
        AccountDeleteDialog(
            onClose = {},
            onAccountDeleteTextChange = {},
            onConfirmClick = {},
            accountDeleteString = "",
        )
    }
}