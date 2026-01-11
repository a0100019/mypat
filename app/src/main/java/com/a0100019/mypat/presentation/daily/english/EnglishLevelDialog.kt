package com.a0100019.mypat.presentation.daily.english

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
import androidx.compose.material3.MaterialTheme
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
fun EnglishLevelDialog(
    onCloseClick: () -> Unit = {},
    onEasyClick: () -> Unit = {},
    onHardClick: () -> Unit = {},
) {

    Dialog(
        onDismissRequest = onCloseClick
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
                MainButton(
                    text = "닫기",
                    onClick = onCloseClick,
                    modifier = Modifier.align(Alignment.End)
                )

                Text(
                    text = "문제 난이도 설정",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "오늘 퀴즈의 난이도를 선택해주세요.\n어려움을 선택할 경우 보상이 두 배가 됩니다.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = 6.dp, end = 6.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 추가로 원하는 Composable 요소

                Row {
                    MainButton(
                        text = "쉬움",
                        onClick = onEasyClick,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    MainButton(
                        text = "어려움",
                        onClick = onHardClick,
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
fun EnglishLevelDialogPreview() {
    MypatTheme {
        EnglishLevelDialog(
            onEasyClick = {},
            onHardClick = {},
        )
    }
}