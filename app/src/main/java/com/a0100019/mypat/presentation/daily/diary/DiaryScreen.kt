package com.a0100019.mypat.presentation.daily.diary

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

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
        diaryDataList = diaryState.diaryDataList,

        clickDiaryData = diaryState.clickDiaryData,

        onDiaryClick = diaryViewModel::onDiaryClick,
        onCloseClick = diaryViewModel::onCloseClick,
        onDiaryChangeClick = diaryViewModel::onDiaryChangeClick
    )
}



@Composable
fun DiaryScreen(
    diaryDataList: List<Diary>,

    clickDiaryData: Diary?,

    onDiaryClick: (Diary) -> Unit,
    onCloseClick: () -> Unit,
    onDiaryChangeClick: () -> Unit
) {

    if(clickDiaryData != null) {
        DiaryReadDialog(
            onClose = onCloseClick,
            diaryData = clickDiaryData,
            onDiaryChangeClick = onDiaryChangeClick
        )
    }
    
    // Fullscreen container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Optional: Set background color
    ) {
        // Text in the center
        Text(
            text = "일기장",
            fontSize = 32.sp, // Large font size
            fontWeight = FontWeight.Bold, // Bold text
            color = Color.Black // Text color
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // 카드 사이 간격 추가
        ) {
            itemsIndexed(diaryDataList.reversed()) { index, diaryData ->

                //달이 다르면 false 출력
                val monthChange = index > 0 && diaryData.date.substring(5, 7) != diaryDataList[index -1].date.substring(5, 7)

                if(monthChange) {
                    Text(diaryData.date.substring(0,7))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(12.dp), // 둥근 테두리
                    elevation = CardDefaults.elevatedCardElevation(4.dp), // 그림자 효과
                    onClick = { onDiaryClick(diaryData) }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(diaryData.date)

                        if(diaryData.title == "") {
                            Text("눌러서 일기를 작성해주세요.")
                        } else {
                            Text(diaryData.title)
                            Text(diaryData.contents)
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

            onDiaryClick = {},
            onCloseClick = {},
            onDiaryChangeClick = {},

            diaryDataList = listOf(
                Diary(date = "2025-02-06", mood = "happy", title = "안녕", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-07", mood = "", title = "", contents = ""),
                Diary(date = "2025-02-06", mood = "happy", title = "안녕", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-07", mood = "", title = "", contents = ""),
                Diary(date = "2025-02-06", mood = "happy", title = "안녕", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-07", mood = "", title = "", contents = ""),
                Diary(date = "2025-02-06", mood = "happy", title = "안녕", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-07", mood = "", title = "", contents = ""),
                Diary(date = "2025-02-06", mood = "happy", title = "안녕", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-07", mood = "", title = "", contents = ""),
                Diary(date = "2025-02-06", mood = "happy", title = "안녕", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-07", mood = "", title = "", contents = ""),
                Diary(date = "2025-01-06", mood = "happy", title = "안녕", contents = "안녕안녕안녕"),
                Diary(date = "2025-02-07", mood = "", title = "", contents = "")
            ),

        )
    }
}