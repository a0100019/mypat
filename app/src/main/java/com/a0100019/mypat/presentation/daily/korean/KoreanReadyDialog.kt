package com.a0100019.mypat.presentation.daily.korean

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.presentation.ui.component.CuteIconButton
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
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
//                .fillMaxHeight(0.8f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
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
                    color = Color.Black
                )

                Text(
                    text = koreanData.meaning,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                    )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                ) {
                    Text(
                        text = koreanCharacter1.last().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                    )

                    Text(
                        text = koreanCharacter2.last().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                    )

                    Text(
                        text = koreanCharacter3.last().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                    )

                    Text(
                        text = koreanCharacter4.last().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                    )

                }

                Text(
                    text = informationText,
                    style = MaterialTheme.typography.titleSmall
                )

                val rows = koreanCharacterList.chunked(5) // 한 줄에 5개씩

                Column {
                    rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { word ->
                                val lastChar = word.takeLast(1)
                                val front = word.dropLast(2)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable {
                                            onKoreanCharacterClick(word)
                                        }
                                ) {
                                    Text(
                                        text = lastChar,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = front,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp)) // 줄 간 간격
                    }
                }

                CuteIconButton(
                    text = "제출",
                    onClick = onSubmitClick,
                    modifier = Modifier
                        .align(Alignment.End)
//                        .padding(16.dp)
                )

                CuteIconButton(
                    text = "x",
                    onClick = onKoreanDeleteClick,

                )

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
        )
    }
}