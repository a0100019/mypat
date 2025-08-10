package com.a0100019.mypat.presentation.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameChangeDialog(
    onClose: () -> Unit,
    onNameTextChange: (String) -> Unit,
    newName: String,
    userData: List<User>,
    onConfirmClick: () -> Unit,
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                    text = "닉네임 변경",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp),
                )

                Text(text = "기존 닉네임 : ${userData.find { it.id == "name" }?.value}")

                OutlinedTextField(
                    value = newName,
                    onValueChange = {
                        // 최대 10자까지 입력 허용
                        if (it.length <= 10) onNameTextChange(it)
                    },
                    label = { Text("닉네임") },
                    placeholder = { Text("새 닉네임을 입력하세요.") },
                    singleLine = true,
//                    isError = !isNameValid, // ❗ 에러 여부
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        focusedBorderColor = if (isNameValid) Color.Blue else Color.Red,
//                        unfocusedBorderColor = if (isNameValid) Color.Gray else Color.Red
//                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "부적절한 닉네임을 사용할 경우, 경고 없이 제제를 받을 수 있습니다",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 6.dp, end = 6.dp)
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
fun NameChangeDialogPreview() {
    MypatTheme {
        NameChangeDialog(
            onClose = {},
            onNameTextChange = {},
            onConfirmClick = {},
            newName = "",
            userData = emptyList()
        )
    }
}