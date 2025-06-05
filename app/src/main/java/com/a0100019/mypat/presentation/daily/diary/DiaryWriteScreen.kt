package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.component.CuteIconButton
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

    //화면 꺼졌을 때 실행하는 코드
    DisposableEffect(Unit) {
        onDispose {
            // 화면이 사라질 때 호출됨 (뒤로가기 포함)
            diaryViewModel.loadData()
        }
    }

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
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "일기장",
            style = MaterialTheme.typography.displayMedium, // Large font size
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
            ,
            contentAlignment = Alignment.Center
        ) {

            // 가운데 텍스트
            Text(
                text = writeDiaryData.date,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    onClick = { onDialogStateChange("emotion") },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    JustImage(
                        filePath = writeDiaryData.emotion,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 오른쪽 버튼
                CuteIconButton(
                    modifier = Modifier,
                    onClick = {
                        onDiaryFinishClick()
                        if (writePossible) {
                            popBackStack()
                        }
                    },
                    text = "작성 완료"
                )

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
            writeDiaryData = Diary(date = "2025-02-06", emotion = "emotion/smile.png", contents = "안녕안녕안녕"),
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