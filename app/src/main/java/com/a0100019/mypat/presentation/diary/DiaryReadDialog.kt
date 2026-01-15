package com.a0100019.mypat.presentation.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.diary.Diary
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DiaryReadDialog(
    onClose: () -> Unit,
    onDiaryChangeClick: () -> Unit,
    diaryData: Diary
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .width(340.dp)
                .fillMaxHeight(0.75f)
                .shadow(20.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White) // 순백색 배경
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp) // 전체적인 여백 확보
            ) {
                // --- 헤더: 날짜와 감정 아이콘 ---
                Row(
                    verticalAlignment = Alignment.Bottom, // 날짜와 요일의 높이 정렬
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = diaryData.date,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val date = LocalDate.parse(diaryData.date, formatter)
                        Text(
                            text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary // 요일에 포인트 컬러
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 감정 아이콘을 강조하는 부드러운 배경
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFF5F5F5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        JustImage(
                            filePath = diaryData.emotion,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 본문: 종이 질감 느낌의 영역 ---
                val scrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF9F9F9), // 아주 연한 회색으로 본문 분리
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = diaryData.contents,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 26.sp, // 읽기 편한 줄간격
                            color = Color(0xFF444444)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 하단 버튼: 미니멀한 디자인 ---
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 수정 버튼 (연한 파스텔톤)
                    TextButton(
                        onClick = onDiaryChangeClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("수정하기", fontWeight = FontWeight.SemiBold)
                    }

                    // 닫기 버튼 (깔끔한 회색톤)
                    TextButton(
                        onClick = onClose,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color(0xFFEEEEEE),
                            contentColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("닫기", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DiaryReadDialogPreview() {
    MypatTheme {
        DiaryReadDialog(
            diaryData = Diary(date = "2024-04-02", emotion = "emotion/smile.png", contents = "내용"),
            onClose = {  },
            onDiaryChangeClick = {}
        )
    }
}