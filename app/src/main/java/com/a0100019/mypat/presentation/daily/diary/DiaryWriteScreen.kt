package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.etc.KoreanIdiomImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState


@Composable
fun DiaryWriteScreen(
    diaryViewModel: DiaryViewModel = hiltViewModel(),
    popBackStack: () -> Unit

) {

    val diaryState : DiaryState = diaryViewModel.collectAsState().value

    val context = LocalContext.current

//    diaryViewModel.collectSideEffect { sideEffect ->
//        when (sideEffect) {
//            is DiarySideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
//        }
//    }

    DiaryWriteScreen(
        value = "스크린 나누기",
        writeDiaryData = diaryState.writeDiaryData,

        onTitleTextChange = diaryViewModel::onTitleTextChange,
        onContentsTextChange = diaryViewModel::onContentsTextChange,
        onDiaryFinishClick = diaryViewModel::onDiaryFinishClick,
        popBackStack = popBackStack
    )
}



@Composable
fun DiaryWriteScreen(
    value: String,
    writeDiaryData: Diary,

    onTitleTextChange: (String) -> Unit,
    onDiaryFinishClick: () -> Unit,
    onContentsTextChange: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    // Fullscreen container
    Column {
        Text("일기장")
        Row {
            Text(writeDiaryData.date)
            Button(
                onClick = {}
            ) {
                Text("감정")
            }
        }

        OutlinedTextField(
            value = writeDiaryData.title,
            onValueChange = onTitleTextChange,
            label = { Text("제목") },
            placeholder = { Text("제목을 입력하세요") },
            singleLine = true,
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = Color.Blue,
//                unfocusedBorderColor = Color.Gray
//            ),
            shape = RoundedCornerShape(8.dp), // 테두리를 둥글게
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = writeDiaryData.contents,
            onValueChange = onContentsTextChange,
            label = { Text("내용") },
            placeholder = { Text("내용을 입력하세요") },
            shape = RoundedCornerShape(8.dp), // 테두리를 둥글게
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .weight(1f)
        )

        Button(
            modifier = Modifier
                .padding(8.dp),
            onClick = {
                onDiaryFinishClick()
                popBackStack()
            }
        ) {
            Text("작성 완료")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryWriteScreenPreview() {
    MypatTheme {
        DiaryWriteScreen(
            value = "",
            writeDiaryData = Diary(date = "2025-02-06", mood = "happy", title = "안녕", contents = "안녕안녕안녕"),
            onTitleTextChange = {},
            onContentsTextChange = {},
            onDiaryFinishClick =  {},
            popBackStack = {}

            )
    }
}