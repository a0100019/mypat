package com.a0100019.mypat.presentation.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.CuteIconButton
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

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
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
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
                    color = Color.Black
                )

                Text(text = "기존 닉네임 : ${userData.find { it.id == "name" }?.value}")

                OutlinedTextField(
                    value = newName,
                    onValueChange = onNameTextChange,
                    label = { Text("닉네임") },
                    placeholder = { Text("새 닉네임을 입력하세요.") },
                    singleLine = true,
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

                Row {
                    CuteIconButton(
                        text = " 취소 ",
                        onClick = onClose,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    CuteIconButton(
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