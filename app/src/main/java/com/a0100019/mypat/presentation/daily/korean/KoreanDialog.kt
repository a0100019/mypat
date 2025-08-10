package com.a0100019.mypat.presentation.daily.korean

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.koreanIdiom.KoreanIdiom
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun KoreanDialog(
    onClose: () -> Unit,
    koreanData: KoreanIdiom,
    onStateChangeClick: () -> Unit,
    koreanDataState: String,
) {

    Dialog(onDismissRequest = onClose) {
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
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {

                // ⭐ 별 아이콘
                Box(modifier = Modifier.fillMaxWidth()) {
                    val starIcon = if (koreanDataState == "별") R.drawable.star_yellow else R.drawable.star_gray
                    Image(
                        painter = painterResource(id = starIcon),
                        contentDescription = "Star Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .clickable { onStateChangeClick() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 큰 텍스트들
                Text(
                    text = koreanData.korean,
                    style = MaterialTheme.typography.headlineLarge,
                    letterSpacing = 8.sp, // 글자 간격 추가
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = koreanData.idiom,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black,
                    letterSpacing = 16.sp, // 글자 간격 추가
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 단어 4개
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(koreanData.korean1)
                    Text(koreanData.korean2)
                    Text(koreanData.korean3)
                    Text(koreanData.korean4)
                }

                Spacer(modifier = Modifier.height(36.dp))

                // 의미
                Text(
                    text = koreanData.meaning,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "획득 날짜 : ${koreanData.date}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End),
                    color = Color.Gray
                )


                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    MainButton(
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
fun KoreanDialogPreview() {
    MypatTheme {
        KoreanDialog(
            onClose = {},
            koreanData = KoreanIdiom(),
            onStateChangeClick = {},
            koreanDataState = "완료"
        )
    }
}