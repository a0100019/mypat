package com.a0100019.mypat.presentation.daily.diary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import java.time.format.TextStyle
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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.a0100019.mypat.presentation.main.management.BannerAd
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

@OptIn(ExperimentalFoundationApi::class)
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

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    // 🔹 감정 선택 다이얼로그
    if (dialogState == "emotion") {
        DiaryEmotionDialog(
            onClose = { onDialogStateChange("") },
            onEmotionClick = emotionChangeClick
        )
    }

    // 🔹 작성 완료 다이얼로그
    if (writeFinish) {
        DiaryFinishDialog(onClose = { popBackStack() })
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 🌿 배경 이미지
        BackGroundImage(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding() // ✅ 키보드 즉시 대응
                .padding(24.dp)
        ) {

            /* ───────── 상단 헤더 ───────── */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 날짜
                Column {
                    val dateText = try {
                        val parsed = LocalDate.parse(writeDiaryData.date)
                        val day = parsed.dayOfWeek.getDisplayName(
                            TextStyle.SHORT,
                            Locale.KOREAN
                        )
                        val formatter = DateTimeFormatter.ofPattern("MM월 dd일")
                        "${parsed.format(formatter)} ($day)"
                    } catch (e: Exception) {
                        writeDiaryData.date
                    }

                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "오늘의 기록",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {

                    // 💾 저장 버튼 (작게)
                    Text(
                        text = "저장",
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (writePossible)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.LightGray
                            )
                            .clickable(enabled = writePossible) {
                                onDiaryFinishClick()
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // 😊 감정 버튼
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .clickable { onDialogStateChange("emotion") },
                        contentAlignment = Alignment.Center
                    ) {
                        JustImage(
                            filePath = writeDiaryData.emotion,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            /* ───────── 일기 입력 영역 ───────── */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF2F2F2).copy(alpha = 0.85f))
                    .padding(16.dp)
                    .bringIntoViewRequester(bringIntoViewRequester)
            ) {
                BasicTextField(
                    value = writeDiaryData.contents,
                    onValueChange = onContentsTextChange,
                    modifier = Modifier
                        .fillMaxSize()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                coroutineScope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    decorationBox = { innerTextField ->
                        if (writeDiaryData.contents.isEmpty()) {
                            Text(
                                text = "가볍게 하루를 정리해볼까요?\n\n" +
                                        "오늘 뭐 했는지, 기분은 어땠는지\n" +
                                        "그냥 생각나는 대로 툭툭 써봐요.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                lineHeight = 28.sp
                            )
                        }
                        innerTextField()
                    }
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
