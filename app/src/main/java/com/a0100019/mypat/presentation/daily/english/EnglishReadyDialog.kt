package com.a0100019.mypat.presentation.daily.english

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
fun EnglishReadyDialog(

    englishTextList: List<String> = listOf(" ", " ", " ", " ", " "),
    failEnglishList: List<String> = emptyList(),
    failEnglishStateList: List<String> = emptyList(),

    onClose: () -> Unit = {},
    onAlphabetDeleteClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onAlphabetClick: (String) -> Unit = {},

    ) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
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
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "영어 단어",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                    ,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(top = 12.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        )
                ) {
                    repeat(5) { index ->
                        Text(
                            text = englishTextList[index].ifEmpty { " " },
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                )
                                .padding(8.dp)
                        )

                    }

                }

                Spacer(modifier = Modifier.size(12.dp))

                if(failEnglishList.isNotEmpty()){
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 12.dp)
                    ) {
                        itemsIndexed(failEnglishList.reversed()) { index, word ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                repeat(5) {
                                    Text(
                                        text = word[it].toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier
                                            .background(
                                                color = if (failEnglishStateList.reversed()[index][it] == '0') {
                                                    Color.Unspecified
                                                } else if (failEnglishStateList.reversed()[index][it] == '1') {
                                                    Color(0xFFFFF59D)
                                                } else {
                                                    Color(0xFFA5D6A7)
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }

                            }
                            Spacer(modifier = Modifier.size(12.dp))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f),  // 전체 영역 차지
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "오늘의 단어를 찾아주세요!\n정답에 포함된 알파벳이면 노란색, 위치까지 일치하면 초록색으로 표시됩니다",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(16.dp) // 여백은 보기 좋게 추가
                        )
                    }

                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))  // 줄 간 간격

                    val alphabetList = listOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p')
                    alphabetList.chunked(10).forEach { rowList ->  // 10개씩 나눔
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowList.forEach { char ->
                                Text(
                                    text = char.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .clickable {
                                            onAlphabetClick(char.toString())
                                        }
                                        .weight(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))  // 줄 간 간격
                    }

                    val alphabetList2 = listOf('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l')
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Spacer(modifier = Modifier.weight(0.1f))
                        alphabetList2.forEach { char ->
                            Text(
                                text = char.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable {
                                        onAlphabetClick(char.toString())
                                    }
                                    .weight(0.1f)
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))  // 줄 간 간격

                    val alphabetList3 = listOf('z', 'x', 'c', 'v', 'b', 'n', 'm')
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Spacer(modifier = Modifier.weight(0.2f))
                        alphabetList3.forEach { char ->
                            Text(
                                text = char.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable {
                                        onAlphabetClick(char.toString())
                                    }
                                    .weight(0.1f)
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.2f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))  // 줄 간 간격

                }

                Spacer(modifier = Modifier.height(16.dp)) // 줄 간 간격

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    MainButton(
                        text = "확인",
                        onClick = onSubmitClick,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                    )

                    Spacer(modifier = Modifier.size(10.dp)) // 나머지 공간 확보

                    MainButton(
                        text = " 지우기 ",
                        onClick = onAlphabetDeleteClick,
                        modifier = Modifier
                        // weight 없이 자동으로 우측으로 감 (Spacer 덕분에)
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnglishReadyDialogPreview() {
    MypatTheme {
        EnglishReadyDialog(
            failEnglishList = listOf("happy", "iiiii"),
            failEnglishStateList = listOf("01210", "12221"),
            englishTextList = listOf("a", "a", "a", "", ""),
        )
    }
}