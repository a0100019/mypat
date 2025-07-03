package com.a0100019.mypat.presentation.daily.diary

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.component.SparkleText
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
        onSearchClearClick = diaryViewModel::onSearchClearClick
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
    }
    
    // Fullscreen container
    Column(
        modifier = Modifier
            .fillMaxSize()
        ,

    ) {
        // Text in the center

        Text(
            text = "일기장",
            style = MaterialTheme.typography.displayMedium, // Large font size
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = {
                    onDialogStateChange("감정")
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.onPrimaryContainer)
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
                text = "검색"
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // 카드 사이 간격 추가
        ) {
            itemsIndexed(diaryDataList) { index, diaryData ->

                val monthChange = index > 0 && diaryData.date.substring(5, 7) != diaryDataList[index -1].date.substring(5, 7)

                if(monthChange) {
                    Text(
                        text = diaryData.date.substring(0,7),
                        modifier = Modifier
                            .padding(start = 16.dp)
                    )
                }

                if(diaryData.state =="대기") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(6.dp),
                        onClick = { onDiaryClick(diaryData) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
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
                                    style = MaterialTheme.typography.bodyMedium,
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
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(6.dp),
                        onClick = { onDiaryClick(diaryData) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                            ,
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
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(start = 6.dp, end = 6.dp)
                                    .fillMaxWidth()
                            )

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