package com.a0100019.mypat.presentation.daily.diary

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.component.SparkleText
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DiaryScreen(
    diaryViewModel: DiaryViewModel = hiltViewModel(),

    onDiaryClick: () -> Unit,
    popBackStack: () -> Unit = {},

    ) {

    val diaryState : DiaryState = diaryViewModel.collectAsState().value

    val context = LocalContext.current

    diaryViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DiarySideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            DiarySideEffect.NavigateToDiaryWriteScreen -> onDiaryClick()
        }
    }

    DiaryScreen(
        diaryDataList = diaryState.diaryFilterDataList,

        clickDiaryData = diaryState.clickDiaryData,
        dialogState = diaryState.dialogState,
        searchText = diaryState.searchText,
        emotionFilter = diaryState.emotionFilter,

        onDiaryClick = diaryViewModel::onDiaryClick,
        onCloseClick = diaryViewModel::onCloseClick,
        onDiaryChangeClick = diaryViewModel::onDiaryChangeClick,
        onSearchClick = diaryViewModel::onSearchClick,
        onSearchTextChange = diaryViewModel::onSearchTextChange,
        onDialogStateChange = diaryViewModel::onDialogStateChange,
        onEmotionFilterClick = diaryViewModel::onEmotionFilterClick,
        onSearchClearClick = diaryViewModel::onSearchClearClick,
        popBackStack = popBackStack
    )
}

@Composable
fun DiaryScreen(
    diaryDataList: List<Diary>,

    clickDiaryData: Diary?,
    dialogState: String,
    searchText: String,
    emotionFilter: String,

    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onDiaryClick: (Diary) -> Unit,
    onCloseClick: () -> Unit,
    onDiaryChangeClick: () -> Unit,
    onDialogStateChange: (String) -> Unit,
    onEmotionFilterClick: (String) -> Unit,
    onSearchClearClick: () -> Unit,
    popBackStack: () -> Unit = {},
) {

    if(clickDiaryData != null) {
        DiaryReadDialog(
            onClose = onCloseClick,
            diaryData = clickDiaryData,
            onDiaryChangeClick = onDiaryChangeClick
        )
    }

    when(dialogState) {
        "검색" -> DiarySearchDialog(
            onClose = onSearchClearClick,
            onSearchTextChange = onSearchTextChange,
            searchString = searchText,
            onConfirmClick = onSearchClick,
        )
        "감정" -> DiaryEmotionDialog(
            onClose = onCloseClick,
            onEmotionClick = onEmotionFilterClick,
            removeEmotion = true
        )
        "달력" -> DiaryCalendarDialog(
            onClose = {  }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        // Fullscreen container
        Column(
            modifier = Modifier
                .fillMaxSize(),

            ) {
            // Text in the center

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "일기장",
                    style = MaterialTheme.typography.displayMedium, // Large font size
                    modifier = Modifier
                )

                // 오른쪽 버튼
                MainButton(
                    text = "닫기",
                    onClick = popBackStack,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 24.dp, start = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                MainButton(
                    onClick = {
                        onDialogStateChange("달력")
                    },
                    text = " 달력 보기 "
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        onDialogStateChange("감정")
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.scrim
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer)
                ) {
                    JustImage(
                        filePath = emotionFilter,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                MainButton(
                    onClick = {
                        onDialogStateChange("검색")
                    },
                    text = " 검색 "
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp) // 카드 사이 간격 추가
            ) {
                itemsIndexed(diaryDataList) { index, diaryData ->

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        label = "scale"
                    )

                    val monthChange = index > 0 && diaryData.date.substring(
                        5,
                        7
                    ) != diaryDataList[index - 1].date.substring(5, 7)

                    if (monthChange) {
                        Text(
                            text = diaryData.date.substring(0, 7),
                            modifier = Modifier
                                .padding(start = 24.dp)
                        )
                    }

                    if (diaryData.state == "대기") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onDiaryClick(diaryData) }
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.scrim
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(
                                    modifier = Modifier
                                        .padding(6.dp)
                                ) {
                                    Row {
                                        Text(
                                            text = diaryData.date,
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        //요일 알려주는 코드
                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        val date = LocalDate.parse(diaryData.date, formatter)
                                        Text(
                                            text = date.dayOfWeek.getDisplayName(
                                                TextStyle.FULL,
                                                Locale.KOREAN
                                            ),
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier
                                                .padding(start = 6.dp)
                                        )
                                    }

                                    Text(
                                        text = "눌러서 일기를 작성해주세요",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                SparkleText(
                                    text = "new!!",
                                    fontSize = 20,
                                    modifier = Modifier
                                        .padding(end = 32.dp)
                                )

                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onDiaryClick(diaryData) }
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.scrim
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = diaryData.date,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier
                                            .padding(start = 6.dp)
                                    )
                                    //요일 알려주는 코드
                                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                    val date = LocalDate.parse(diaryData.date, formatter)
                                    Text(
                                        text = date.dayOfWeek.getDisplayName(
                                            TextStyle.FULL,
                                            Locale.KOREAN
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .padding(start = 6.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    JustImage(
                                        filePath = diaryData.emotion,
                                        modifier = Modifier
                                            .size(25.dp)
                                            .padding(end = 8.dp)
                                    )
                                }
                                Text(
                                    text = diaryData.contents,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .fillMaxWidth(),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryScreenPreview() {
    MypatTheme {
        DiaryScreen(

            clickDiaryData = null,
            dialogState = "",
            searchText = "",

            onDiaryClick = {},
            onCloseClick = {},
            onDiaryChangeClick = {},
            onSearchClick = {},
            onSearchTextChange = {},
            onDialogStateChange = {},
            onEmotionFilterClick = {},
            onSearchClearClick = {},
            emotionFilter = "etc/snowball.png",

            diaryDataList = listOf(
                Diary(date = "2025-02-07", emotion = "", contents = ""),
                Diary(date = "2025-02-06", emotion = "emotion/smile.png", contents = "안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕안녕", state = "완료"),
                Diary(date = "2025-02-07", emotion = "", contents = ""),
                Diary(date = "2025-02-06", emotion = "happy", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-07", emotion = "", contents = ""),
                Diary(date = "2025-01-05", emotion = "happy", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-06", emotion = "", contents = ""),
                Diary(date = "2025-02-07", emotion = "happy", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-08", emotion = "", contents = "")
            ),

        )
    }
}