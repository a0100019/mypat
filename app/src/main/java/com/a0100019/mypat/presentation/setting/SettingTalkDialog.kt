package com.a0100019.mypat.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun SettingTalkDialog(
    onClose: () -> Unit,
    onSettingTalkTextChange: (String) -> Unit,
    settingTalkText: String,
    onConfirmClick: () -> Unit,
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
                    text = "하고 싶은 말을 적어주세요",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(10.dp),
                )

                Text(
                    text = "어떤 내용이든 좋아요!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp),
                )

                OutlinedTextField(
                    value = settingTalkText,
                    onValueChange = onSettingTalkTextChange,
                    label = { Text("내용") },
                    placeholder = { Text(
                        textAlign = TextAlign.Center,
                        text = "하루마을이 더욱 발전할 수 있도록, 발견한 버그나 바라는 점을 적어주세요. 중요한 버그를 발견하면 보상을 받을 수도 있어요.\n" +
                                "또한 대나무 숲에 작성한 내용은 오직 개발자만 확인할 수 있으니 말 못할 고민이 있을 땐 언제든 편하게 적어주세요. 하루마을과 관련 없는 내용도 괜찮아요!\n" +
                                "답변이 필요한 내용이라면 편지로 답변을 보내드릴게요") },
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
fun SettingTalkDialogPreview() {
    MypatTheme {
        SettingTalkDialog(
            onClose = {},
            onSettingTalkTextChange = {},
            onConfirmClick = {},
            settingTalkText = "",
        )
    }
}