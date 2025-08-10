package com.a0100019.mypat.presentation.daily.korean

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun KoreanReadyDialog(

    onClose: () -> Unit,
    koreanData: KoreanIdiom,
    onKoreanDeleteClick: () -> Unit = {},
    onSubmitClick: () -> Unit,
    koreanCharacterList: List<String> = emptyList(),
    koreanCharacter1: String = "",
    koreanCharacter2: String = "",
    koreanCharacter3: String = "",
    koreanCharacter4: String = "",
    onKoreanCharacterClick: (String) -> Unit = {},
    informationText: String = "",


) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                    ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = koreanData.idiom,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                    ,
                )

                Text(
                    text = koreanData.meaning,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                    )

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        )
                ) {
                    Text(
                        text = koreanCharacter1.takeLast(1).ifEmpty { " " },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                            )
                            .padding(8.dp)
                    )

                    Text(
                        text = koreanCharacter2.takeLast(1).ifEmpty { " " },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                            )
                            .padding(8.dp)
                    )

                    Text(
                        text = koreanCharacter3.takeLast(1).ifEmpty { " " },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                            )
                            .padding(8.dp)
                    )

                    Text(
                        text = koreanCharacter4.takeLast(1).ifEmpty { " " },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primaryContainer,
                            )
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = informationText,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(16.dp)) // 줄 간 간격

                val rows = koreanCharacterList.chunked(5) // 한 줄에 5개씩
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.Gray, // 원하는 색상
                            shape = RoundedCornerShape(8.dp) // 원하면 모서리 둥글게
                        )
                        .padding(8.dp) // 테두리와 내부 내용 사이 여백
                ) {
                    Spacer(modifier = Modifier.size(8.dp))

                    rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { word ->
                                val lastChar = word.takeLast(1).ifEmpty { " " }
                                val front = word.dropLast(2).ifEmpty { " " }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable { onKoreanCharacterClick(word) }
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = lastChar,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = front,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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
                        onClick = onKoreanDeleteClick,
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
fun KoreanReadyDialogPreview() {
    MypatTheme {
        KoreanReadyDialog(
            onClose = {},
            koreanData = KoreanIdiom(),
            onSubmitClick = {},
            koreanCharacter1 = "ㄱ",
            koreanCharacter2 = "s",
            koreanCharacter3 = "a",
            koreanCharacter4 = "b",
            informationText = "aa",
            koreanCharacterList = listOf("aa a", "aahha a", "aa a", "aa a", "aa a", "aa a", "aa a", "aa a", "aa a", "aa a", )
        )
    }
}