package com.a0100019.mypat.presentation.daily.walk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@Composable
fun CalendarView(
    today: String,
    calendarMonth: String,
    stepsRaw: String = ""
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val yearMonth = YearMonth.parse(calendarMonth, formatter)

    val year = yearMonth.year
    val month = yearMonth.monthValue

    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

    // "/" 기준으로 날짜 목록 나누기
    val items = stepsRaw.split("/").filter { it.isNotBlank() }

    // 🔥 walkList → Map<String, Int> 변환
    val walkMap = items
        .mapNotNull {
            val parts = it.split(".")
            if (parts.size == 2) parts[0] to parts[1].toInt() else null
        }
        .toMap()

    val dates = mutableListOf<LocalDate?>()
    repeat(startDayOfWeek) { dates.add(null) }
    repeat(daysInMonth) { dates.add(firstDayOfMonth.plusDays(it.toLong())) }

    val totalCells = ((dates.size + 6) / 7) * 7
    repeat(totalCells - dates.size) { dates.add(null) }

    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEachIndexed { index, day ->
                val textColor = when (index) {
                    0 -> Color(0xFFFF8A80)
                    6 -> Color(0xFF64B5F6)
                    else -> Color.Unspecified
                }
                Text(
                    text = day,
                    color = textColor,
                    modifier = Modifier.weight(1f).padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        val todayDate = LocalDate.parse(today)

        dates.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                week.forEachIndexed { index, date ->
                    val dateString = date?.toString()
                    val count = walkMap[dateString] // 🔥 날짜가 있으면 숫자

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            // 날짜 숫자
                            Text(
                                text = date?.dayOfMonth?.toString() ?: "",
                                textAlign = TextAlign.Center
                            )

                            // 🔥 날짜가 walkList에 있으면 → count 표시
                            if (count != null) {
                                Text(
                                    text = count.toString(),
                                    color = Color(0xFF00897B), // 예쁜 색
                                    modifier = Modifier.padding(top = 2.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
