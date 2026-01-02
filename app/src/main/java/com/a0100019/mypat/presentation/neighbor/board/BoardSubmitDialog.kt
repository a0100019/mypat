package com.a0100019.mypat.presentation.neighbor.board

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
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
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun BoardSubmitDialog(
    text: String = "",
    anonymous: String = "0",
    type: String = "free",

    onClose: () -> Unit ={},
    onChangeAnonymousClick: (String) -> Unit ={},
    onChangeTypeClick: (String) -> Unit ={},
    onTextChange: (String) -> Unit ={},
    onConfirmClick: () -> Unit ={},
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
                .padding(12.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "게시글 작성",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = anonymous == "1",
                        onCheckedChange = {
                            onChangeAnonymousClick(if (it) "1" else "0")
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = "익명으로 작성")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 자유
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = type == "free",
                            onCheckedChange = {
                                if (it) onChangeTypeClick("free")
                            }
                        )
                        Text(text = "자유")
                    }

                    // 고민
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = type == "worry",
                            onCheckedChange = {
                                if (it) onChangeTypeClick("worry")
                            }
                        )
                        Text(text = "고민")
                    }

                    // 축하
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = type == "congratulation",
                            onCheckedChange = {
                                if (it) onChangeTypeClick("congratulation")
                            }
                        )
                        Text(text = "축하")
                    }

                    // 친구
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = type == "friend",
                            onCheckedChange = {
                                if (it) onChangeTypeClick("friend")
                            }
                        )
                        Text(
                            text = "친구\n구하기",
                            textAlign = TextAlign.Center,
                        )
                    }


                }

                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text("내용") },
                    placeholder = { Text("내용을 입력하세요.") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)   // 🔥 고정 높이
                        .padding(8.dp),
                    maxLines = Int.MAX_VALUE // 여러 줄 입력 가능
                )


                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "부적절한 내용을 작성할 경우, 경고 없이 제제를 받을 수 있습니다",
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
fun BoardSubmitDialogPreview() {
    MypatTheme {
        BoardSubmitDialog(
        )
    }
}