package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DiarySearchDialog(
    onClose: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    searchString: String,
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
            Column(modifier = Modifier) {

                Text(
                    text = "검색",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black
                )

                OutlinedTextField(
                    value = searchString,
                    onValueChange = onSearchTextChange,
                    label = { Text("검색어") },
//                    placeholder = { Text("새 닉네임을 입력하세요.") },
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
                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text("지우기")
                    }

                    Button(
                        onClick = onConfirmClick,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DiarySearchDialogPreview() {
    MypatTheme {
        DiarySearchDialog(
            onClose = {},
            onSearchTextChange = {},
            onConfirmClick = {},
            searchString = ""
        )
    }
}