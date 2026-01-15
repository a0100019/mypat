package com.a0100019.mypat.presentation.diary

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun DiaryCalendarDialog(
    onClose: () -> Unit,
    today: String = "2025-07-15",
    calendarMonth: String = "2025-07",
    onCalendarMonthChangeClick: (String) -> Unit = {},
    diaryDataList: List<Diary> = emptyList(),
    onDiaryDateClick: (String) -> Unit = {}
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .width(360.dp)
                .shadow(20.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 상단 타이틀
                Text(
                    text = "나의 기억들",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 월 선택 컨트롤러
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { onCalendarMonthChangeClick("left") }) {
                        Icon(painterResource(id = R.drawable.arrow), "이전", Modifier.rotate(270f).size(16.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = calendarMonth, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                        Text(
                            text = "오늘로 이동",
                            style = MaterialTheme.typography.labelSmall.copy(textDecoration = TextDecoration.Underline),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onCalendarMonthChangeClick("today") }
                        )
                    }
                    IconButton(onClick = { onCalendarMonthChangeClick("right") }) {
                        Icon(painterResource(id = R.drawable.arrow), "다음", Modifier.rotate(90f).size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 요일 표시
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (day == "일") Color(0xFFFF5252) else if (day == "토") Color(0xFF448AFF) else Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 달력 그리드 연산
                val yearMonth = YearMonth.parse(calendarMonth)
                val firstDayOfMonth = yearMonth.atDay(1)
                val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7
                val daysInMonth = yearMonth.lengthOfMonth()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.height(300.dp),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(dayOfWeekOffset) { Spacer(modifier = Modifier.size(44.dp)) }

                    items(daysInMonth) { day ->
                        val dayNum = day + 1
                        val dateString = String.format("%s-%02d", calendarMonth, dayNum)
                        val diaryEntry = diaryDataList.find { it.date == dateString }

                        CalendarDayItem(
                            dateText = dateString,
                            dayNumber = dayNum.toString(),
                            today = today,
                            diary = diaryEntry,
                            onDateClick = onDiaryDateClick
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 하단 닫기 버튼
                TextButton(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.textButtonColors(containerColor = Color(0xFFF1F1F1), contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("닫기", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    dateText: String,
    dayNumber: String,
    today: String,
    diary: Diary?,
    onDateClick: (String) -> Unit
) {
    val todayDate = LocalDate.parse(today)
    val currentDate = try { LocalDate.parse(dateText) } catch(e: Exception) { null }

    val isFuture = currentDate?.isAfter(todayDate) == true
    val isToday = currentDate?.isEqual(todayDate) == true

    val textColor = when {
        isFuture -> Color(0xFFE0E0E0)
        isToday -> MaterialTheme.colorScheme.primary
        else -> Color(0xFF424242)
    }

    val todayBackground =
        if (isToday) Color(0xFFDFF3EA) else Color.Transparent
    // ↑ 파스텔 민트톤 (너 앱이 숲/힐링 컨셉이라 잘 어울림)

    Box(
        modifier = Modifier
            .aspectRatio(0.8f)
            .padding(2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(todayBackground)
            .clickable(enabled = !isFuture) {
                onDateClick(dateText)
            },
        contentAlignment = Alignment.Center
    ) {

        if (diary != null && diary.state != "대기") {
            // --- 일기 쓴 날 ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                JustImage(
                    filePath = diary.emotion,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = dayNumber,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = textColor.copy(alpha = 0.85f)
                )
            }
        } else {
            // --- 일기 안 쓴 날 ---
            Text(
                text = dayNumber,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isToday)
                        FontWeight.ExtraBold
                    else
                        FontWeight.Medium
                ),
                color = textColor
            )
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