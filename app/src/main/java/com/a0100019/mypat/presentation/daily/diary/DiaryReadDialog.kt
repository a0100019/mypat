package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DiaryReadDialog(
    onClose: () -> Unit,
    onDiaryChangeClick: () -> Unit,
    diaryData: Diary
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = diaryData.date,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(start = 6.dp)
                        )

                    //요일 알려주는 코드
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val date = LocalDate.parse(diaryData.date, formatter)
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(start = 12.dp)
                        )

                    Spacer(modifier = Modifier.weight(1f))

                    JustImage(
                        filePath = diaryData.emotion,
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                val scrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = diaryData.contents,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }


                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    MainButton(
                        text = " 수정 ",
                        onClick = onDiaryChangeClick,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(end = 6.dp)
                    )
                    MainButton(
                        text = " 닫기 ",
                        onClick = onClose,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(end = 6.dp)
                    )
                }


            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DiaryReadDialogPreview() {
    MypatTheme {
        DiaryReadDialog(
            diaryData = Diary(date = "2024-04-02", emotion = "emotion/smile.png", contents = "내용"),
            onClose = {  },
            onDiaryChangeClick = {}
        )
    }
}