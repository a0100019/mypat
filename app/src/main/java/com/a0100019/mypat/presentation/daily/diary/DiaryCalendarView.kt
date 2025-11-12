package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.a0100019.mypat.data.room.diary.Diary
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun DiaryCalendarView(
    today: String,
    calendarMonth: String, // 예: "2025-04"
    diaryList: List<Diary>,
    onDiaryDateClick: (String) -> Unit = {}
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val yearMonth = YearMonth.parse(calendarMonth, formatter)

    val year = yearMonth.year
    val month = yearMonth.monthValue

    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0

    // 걸은 날짜 리스트 (LocalDate 형태로 변환)
    val walkedDates = diaryList.map { LocalDate.parse(it.date) }.toSet()

    val dates = mutableListOf<LocalDate?>()
    repeat(startDayOfWeek) { dates.add(null) }
    repeat(daysInMonth) { dates.add(firstDayOfMonth.plusDays(it.toLong())) }

    // 마지막 주도 7칸 맞추기 위해 빈 칸 추가
    val totalCells = ((dates.size + 6) / 7) * 7
    repeat(totalCells - dates.size) { dates.add(null) }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.5f)
    ) {
        // 요일 헤더
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEachIndexed { index, day ->
                val textColor = when (index) {
                    0 -> Color(0xFFFF8A80) // 파스텔 빨강 (일)
                    6 -> Color(0xFF64B5F6) // 파스텔 파랑 (토)
                    else -> Color.Unspecified
                }

                Text(
                    text = day,
                    color = textColor,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        val todayDate = LocalDate.parse(today) // ← 오늘 날짜

        // 날짜 셀
        dates.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                ,
                verticalAlignment = Alignment.CenterVertically

            ) {
                week.forEachIndexed { index, date ->
                    val isWalked = date != null && date in walkedDates
                    val isToday = date != null && date == todayDate

                    val textColor = when (index) {
                        0 -> Color(0xFFFF8A80) // 일요일
                        6 -> Color(0xFF64B5F6) // 토요일
                        else -> Color.Unspecified
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
//                            .aspectRatio(1.2f)
                            .padding(2.dp)
                        ,
                        contentAlignment = Alignment.Center
                    ) {

                        // 동그란 배경 레이어
                        if (isToday) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFE1BEE7), shape = CircleShape) // 연보라 배경
                            )
                        }

                        if (isWalked) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(Color(0xFFB2EBF2), shape = CircleShape) // 연보라 배경
                            )
                        }

                        // 날짜 텍스트
                        Text(
                            text = date?.dayOfMonth?.toString() ?: "",
                            color = textColor,
                            modifier = Modifier
                                .clickable {
                                    onDiaryDateClick(date.toString())
                                }
                        )
                    }
                }
            }
        }

    }
}