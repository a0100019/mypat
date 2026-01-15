package com.a0100019.mypat.presentation.activity.daily.english

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.english.English
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun EnglishEasyReadyDialog(

    englishTextList: List<String> = listOf(" ", " ", " ", " ", " "),
    clickEnglishDataState: String = "대기",
    clickEnglishData: English = English(),

    onClose: () -> Unit = {},
    onAlphabetDeleteClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onAlphabetClick: (String) -> Unit = {},
    onHintClick: () -> Unit = {}

) {


    Dialog(
        onDismissRequest = onClose
    ) {

        Box(
            modifier = Modifier
                .width(340.dp)
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

                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "close",
                    modifier = Modifier
                        .clickable { onClose() }
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp) // ⬅️ 둥글게 처리
                        )
                        .align(Alignment.End),
                )
                Spacer(modifier = Modifier.size(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "영어 단어",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center),
                    )

                    if(clickEnglishDataState == "쉬움"){
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    onHintClick()
                                }
                        ) {
                            Text(
                                text = "힌트"
                            )
                            Image(
                                painter = painterResource(id = R.drawable.key),
                                contentDescription = "State Icon",
                                modifier = Modifier
                            )
                        }
                    }
                }

                Text(
                    text = "영어 단어의 순서를 맞춰주세요!"
                )

                Spacer(modifier = Modifier.size(36.dp))

                val shuffledWord = remember(clickEnglishData.word) {
                    val original = clickEnglishData.word
                    val firstChar = original.first()

                    generateSequence {
                        original.toList().shuffled().joinToString("")
                    }
                        .first { it.first() != firstChar }
                }

                Text(
                    text = shuffledWord,
                    style = MaterialTheme.typography.headlineMedium,
                    letterSpacing = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )

                Spacer(modifier = Modifier.size(36.dp))

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

                Spacer(modifier = Modifier.size(36.dp))

                if(clickEnglishDataState == "쉬움뜻") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = "뜻 : ${clickEnglishData.meaning}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
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
                                        .background(
                                            color = Color.Unspecified,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .border( // ✅ 둥근 테두리 추가
                                            width = 1.dp,
                                            color = Color.Unspecified,
                                            shape = RoundedCornerShape(8.dp)
                                        )
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
                                    .background(
                                        color = Color.Unspecified,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border( // ✅ 둥근 테두리 추가
                                        width = 1.dp,
                                        color = Color.Unspecified,
                                        shape = RoundedCornerShape(8.dp)
                                    )
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
                                    .background(
                                        color = Color.Unspecified,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border( // ✅ 둥근 테두리 추가
                                        width = 1.dp,
                                        color = Color.Unspecified,
                                        shape = RoundedCornerShape(8.dp)
                                    )
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
fun EnglishEasyReadyDialogPreview() {
    MypatTheme {
        EnglishEasyReadyDialog(
            englishTextList = listOf("a", "a", "a", "", ""),
            clickEnglishData = English(word = "apple")
        )
    }
}