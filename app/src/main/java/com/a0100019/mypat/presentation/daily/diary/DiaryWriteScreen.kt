package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
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
        writeDiaryData = diaryState.writeDiaryData,
        writePossible = diaryState.writePossible,
        isError = diaryState.isError,
        dialogState = diaryState.dialogState,

        onContentsTextChange = diaryViewModel::onContentsTextChange,
        onDiaryFinishClick = diaryViewModel::onDiaryFinishClick,
        popBackStack = popBackStack,
        emotionChangeClick = diaryViewModel::emotionChangeClick,
        onDialogStateChange = diaryViewModel::onDialogStateChange
    )
}



@Composable
fun DiaryWriteScreen(
    writeDiaryData: Diary,
    writePossible: Boolean,
    isError: Boolean,
    dialogState: String,

    onDiaryFinishClick: () -> Unit,
    onContentsTextChange: (String) -> Unit,
    popBackStack: () -> Unit,
    emotionChangeClick: (String) -> Unit,
    onDialogStateChange: (String) -> Unit,
) {

    when(dialogState) {
        "emotion" -> DiaryEmotionDialog(
            onClose = {},
            onEmotionClick = emotionChangeClick
        )
    }

    // Fullscreen container
    Column {
        Text("일기장")
        Row {
            Text(writeDiaryData.date)
            Button(
                onClick = {
                    onDialogStateChange("emotion")
                }
            ) {
                JustImage(filePath = writeDiaryData.emotion)
            }

            Button(
                modifier = Modifier
                    .padding(8.dp),
                onClick = {
                    onDiaryFinishClick()
                    if(writePossible){
                        popBackStack()
                    }
                }
            ) {
                Text("작성 완료")
            }

        }

        OutlinedTextField(
            value = writeDiaryData.contents,
            onValueChange = onContentsTextChange,
            label = { Text("내용") },
            isError = isError,
            placeholder = { Text("내용을 10자 이상 입력하세요") },
            shape = RoundedCornerShape(8.dp), // 테두리를 둥글게
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .weight(1f)
        )


    }
}

@Preview(showBackground = true)
@Composable
fun DiaryWriteScreenPreview() {
    MypatTheme {
        DiaryWriteScreen(
            writeDiaryData = Diary(date = "2025-02-06", emotion = "happy", contents = "안녕안녕안녕"),
            onContentsTextChange = {},
            onDiaryFinishClick =  {},
            popBackStack = {},
            writePossible = false,
            isError = false,
            emotionChangeClick = {},
            dialogState = "",
            onDialogStateChange = {}

            )
    }
}