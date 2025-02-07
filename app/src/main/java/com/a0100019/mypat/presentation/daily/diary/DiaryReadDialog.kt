package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DiaryReadDialog(
    onClose: () -> Unit,
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

                Text(diaryData.date)
                Box(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxSize()
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(diaryData.title)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(diaryData.contents)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row {
                    Button(
                        onClick = {  },
                        modifier = Modifier
                    ) {
                        Text("수정")
                    }
                    Button(
                        onClick = onClose,
                        modifier = Modifier
                    ) {
                        Text("Close")
                    }
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
            diaryData = Diary(date = "2024-04-02", mood = "happy", title = "제목", contents = "내용"),
            onClose = {  }
        )
    }
}