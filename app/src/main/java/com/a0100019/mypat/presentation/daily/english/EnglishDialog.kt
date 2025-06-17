package com.a0100019.mypat.presentation.daily.english

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.english.English
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.presentation.ui.component.CuteIconButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun EnglishDialog(
    onClose: () -> Unit = {},
    english: English = English(),
    onStateChangeClick: () -> Unit = {},
    englishDataState: String = "완료",
) {

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Dialog 바깥 여백
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {

                // ⭐ 별 아이콘
                Box(modifier = Modifier.fillMaxWidth()) {
                    val starIcon = if (englishDataState == "별") R.drawable.star_yellow else R.drawable.star_gray
                    Image(
                        painter = painterResource(id = starIcon),
                        contentDescription = "Star Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .clickable { onStateChangeClick() }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 큰 텍스트들
                Text(
                    text = english.word,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black,
                    letterSpacing = 6.sp, // 글자 간격 추가
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.size(32.dp))

                Text(
                    text = english.meaning,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.DarkGray,
                    letterSpacing = 12.sp, // 글자 간격 추가
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "획득 날짜 : ${english.date}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End),
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    CuteIconButton(
                        onClick = onClose,
                        text = " 닫기 ",
                        modifier = Modifier
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun EnglishDialogPreview() {
    MypatTheme {
        EnglishDialog(
        )
    }
}