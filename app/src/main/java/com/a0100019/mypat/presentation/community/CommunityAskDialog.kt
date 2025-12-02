package com.a0100019.mypat.presentation.community

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
fun CommunityAskDialog(
    onClose: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
    text: String = "",
    onConfirmClick: () -> Unit = {},
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
                    text = "도란도란",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(10.dp),
                )

                Text(
                    text = "도란도란은 평소에 말 못했던 질문이나 고민을 익명으로 올리고 이웃들에게 조언을 받을 수 있는 기능입니다. 검토 후 게시하니 어떤 내용이든 부담없이 작성해주세요!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp),
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text("내용") },
                    placeholder = { Text(
                        textAlign = TextAlign.Center,
                        text = "도란도란이 많을 경우, 선별되어 게시됩니다.\n\n" +
                                "ex) 요즘 미래에 대해 고민이 있어요..\n햄버거 vs 치킨 평생 하나만 먹는다면?!\n" +
                                "시험을 망쳤어요 위로해주세요 ㅠㅠ\n좋아하는 사람이 생겼어요! 어떻게 해야할까요?") },
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
fun CommunityAskDialogPreview() {
    MypatTheme {
        CommunityAskDialog(
            onClose = {},
            onTextChange = {},
            onConfirmClick = {},
            text = "",
        )
    }
}