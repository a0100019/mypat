package com.a0100019.mypat.presentation.daily.diary

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage

@Composable
fun DiaryWriteScreen(
    diaryWriteViewModel: DiaryWriteViewModel = hiltViewModel(),
    popBackStack: () -> Unit
) {
    val diaryWriteState: DiaryWriteState = diaryWriteViewModel.collectAsState().value
    val context = LocalContext.current

    // 뒤로가기 다이얼로그 상태
    var showExitDialog by remember { mutableStateOf(false) }

    // ✅ 다이얼로그 뜰 때는 뒤로가기 비활성화
    BackHandler(enabled = !showExitDialog) {
        showExitDialog = true
    }

    // ✅ 다이얼로그 UI
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "작성 중인 일기가 있어요",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                    )
                    },
            text = { Text(
                text = "정말 나가시겠습니까?\n작성한 내용은 저장되지 않습니다.",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
                   },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false  // 그냥 닫기
                }) {
                    Text("아니오")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    popBackStack()  // 🔥 뒤로 나가기
                }) {
                    Text("네")
                }
            }
        )
    }

    // 아래는 실제 일기 UI
    DiaryWriteScreen(
        writeDiaryData = diaryWriteState.writeDiaryData,
        writePossible = diaryWriteState.writePossible,
        isError = diaryWriteState.isError,
        dialogState = diaryWriteState.dialogState,
        writeFinish = diaryWriteState.writeFinish,
        onContentsTextChange = diaryWriteViewModel::onContentsTextChange,
        onDiaryFinishClick = diaryWriteViewModel::onDiaryFinishClick,
        popBackStack = popBackStack,
        emotionChangeClick = diaryWriteViewModel::emotionChangeClick,
        onDialogStateChange = diaryWriteViewModel::onDialogStateChange,
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
    writeFinish: Boolean = false,
    onLastFinishClick: () -> Unit = {},
) {
    if (dialogState == "emotion") {
        DiaryEmotionDialog(
            onClose = { onDialogStateChange("") },
            onEmotionClick = emotionChangeClick
        )
    }

    if(writeFinish) {
        DiaryFinishDialog(
            onClose = {
                popBackStack()
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 6.dp)
        ) {
            Text(
                text = "일기장",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            val configuration = LocalConfiguration.current
            val screenHeightDp = configuration.screenHeightDp
            val halfHeightDp = (screenHeightDp * 0.5).dp
            OutlinedTextField(
                value = writeDiaryData.contents,
                onValueChange = onContentsTextChange,
                label = { Text("내용") },
                isError = isError,
                placeholder = { Text("내용을 입력하세요") },
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp
                )
                ,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(halfHeightDp)
                    .padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
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
                        onClick = {
                            onDialogStateChange("emotion")
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.scrim
                        ),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        JustImage(
                            filePath = writeDiaryData.emotion,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    MainButton(
                        onClick = {
                            onDiaryFinishClick()
                        },
                        text = "작성 완료"
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Center,
                    text = "가볍게 하루를 정리해볼까요?\n\n" +
                            "오늘 뭐 했는지, 뭐 먹었는지, 기분은 어땠는지\n" +
                            "그냥 생각나는 대로 툭툭 써봐요\n" +
                            "잘 쓰려고 애쓸 필요도 없고\n" +
                            "누구한테 보여줄 것도 아니니까요\n" +
                            "하루를 정리하면, 마음도 조금 정돈될 거예요"
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryWriteScreenPreview() {
    MypatTheme {
        DiaryWriteScreen(
            writeDiaryData = Diary(
                date = "2025-02-06",
                emotion = "emotion/smile.png",
                contents = "안녕안녕안녕"
            ),
            onContentsTextChange = {},
            onDiaryFinishClick = {},
            popBackStack = {},
            writePossible = false,
            isError = false,
            emotionChangeClick = {},
            dialogState = "",
            onDialogStateChange = {}
        )
    }
}
