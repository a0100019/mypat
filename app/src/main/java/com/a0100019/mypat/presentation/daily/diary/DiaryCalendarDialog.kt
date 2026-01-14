package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DiaryCalendarDialog(
    onClose: () -> Unit,
    today: String = "2025-07-15",
    calendarMonth: String = "2025-07",
    onCalendarMonthChangeClick: (String)-> Unit = {},
    diaryDataList: List<Diary> = emptyList(),
    onDiaryDateClick: (String) -> Unit = {}
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "달력",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(8.dp),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "별 아이콘",
                        modifier = Modifier
                            .rotate(270f)
                            .clickable(
                                indication = null, // ← ripple 효과 제거
                                interactionSource = remember { MutableInteractionSource() } // ← 필수
                            ) {
                                onCalendarMonthChangeClick("left")
                            }
                    )
                    Text(
                        text = "오늘로 이동",
                        modifier = Modifier
                            .clickable(
                                indication = null, // ← ripple 효과 제거
                                interactionSource = remember { MutableInteractionSource() } // ← 필수
                            ) {
                                onCalendarMonthChangeClick("today")
                            }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "별 아이콘",
                        modifier = Modifier
                            .rotate(90f)
                            .clickable(
                                indication = null, // ← ripple 효과 제거
                                interactionSource = remember { MutableInteractionSource() } // ← 필수
                            ) {
                                onCalendarMonthChangeClick("right")
                            }
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = calendarMonth,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.size(12.dp))

                DiaryCalendarView(
                    today = today,
                    calendarMonth = calendarMonth,
                    diaryList = diaryDataList,
                    onDiaryDateClick = onDiaryDateClick
                )

                Text(
                    text = "지난 날짜의 일기도 작성할 수 있습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Spacer(modifier = Modifier.weight(1f))

                    MainButton(
                        text = " 닫기 ",
                        onClick = onClose,
                        modifier = Modifier
                            .padding(8.dp)
                    )

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryCalendarDialogPreview() {
    MypatTheme {
        DiaryCalendarDialog(
            onClose = {},
        )
    }
}