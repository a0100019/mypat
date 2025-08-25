package com.a0100019.mypat.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun LetterDialog(
    onClose: () -> Unit,
    onLetterClick: (Int) -> Unit,
    letterDataList: List<Letter>
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .fillMaxHeight(0.9f)
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
                    text = "우체통",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(10.dp),
                )

                Spacer(modifier = Modifier.size(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    items(letterDataList) { letter ->
                        when(letter.state) {
                            "open" -> Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onLetterClick(letter.id) }
                                    .padding(12.dp),
                            ) {
                                Text(
                                    text = letter.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = letter.date,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            "read" -> Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onLetterClick(letter.id) }
                                    .padding(12.dp),
                            ){
                                Text(
                                    text = letter.title,
                                    modifier = Modifier,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = letter.date,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray,
                                )
                            }
                        }
                    }
                }

                MainButton(
                    text = " 닫기 ",
                    onClick = onClose,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.End)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LetterDialogPreview() {
    MypatTheme {
        LetterDialog(
            onClose = {},
            onLetterClick = {},
            letterDataList = listOf(Letter(state = "waiting", title = "첫 편지", message = "안녕하세요 저는 이유빈입니다.\n안녕하세요 저는 이유빈입니다.\n안녕하세요 저는 이유빈입니다.\n", link = "naver.com", reward = "cash", amount = "100" ),
                Letter(state = "open", date = "2025-07-10", title = "2 편지", message = "안녕하세요 저는 이유빈입니다.안녕하세요 저는 이유빈입니다.안녕하세요 저는 이유빈입니다.", link = "naver.com", reward = "cash", amount = "100" ),
                Letter(state = "read", date = "2025-07-11", title = "3 편지", message = "안녕하세요 저는 이유빈입니다.\n안녕하세요 저는 이유빈입니다.\n안녕하세요 저는 이유빈입니다.\n", link = "naver.com", reward = "cash", amount = "100" ),
                Letter(state = "read", date = "2025-07-12", title = "4 편지", message = "안녕하세요 저는 이유빈입니다.\n안녕하세요 저는 이유빈입니다.\n안녕하세요 저는 이유빈입니다.\n", link = "naver.com", reward = "cash", amount = "100" ),
            )
        )
    }
}