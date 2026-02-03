package com.a0100019.mypat.presentation.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DiaryFirstFinishDialog(
    onClose: () -> Unit,
    diarySequence: Int = 0
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                // 그림자를 살짝 색깔 있는 느낌으로 주면 훨씬 감성적입니다
                .shadow(16.dp, RoundedCornerShape(28.dp), ambientColor = Color(0xFF6B8E23))
                .border(
                    width = 3.dp,
                    color = Color(0xFFE0E0E0), // 부드러운 테두리 색상
                    shape = RoundedCornerShape(28.dp)
                )
                .background(
                    brush = Brush.verticalGradient( // 단색보다 은은한 그라데이션이 예쁩니다
                        colors = listOf(Color.White, Color(0xFFF9FFF0))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(vertical = 32.dp, horizontal = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 성공 아이콘 또는 작은 일러스트 (마을 느낌)
                Text(
                    text = "🌱", // 또는 마을 관련 아이콘
                    fontSize = 40.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. 제목 텍스트 (볼드 처리)
                Text(
                    text = "일기 작성 완료!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D5A27) // 진한 숲색
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. 보상 정보 (말풍선 느낌)
                Surface(
                    color = Color(0xFFFFEFA1), // 따뜻한 햇살색
                    shape = CircleShape,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        JustImage(
                            filePath = "etc/sun.png",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = " +3",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF8B6B00)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${diarySequence + 1}일 연속 일기를 작성했어요!"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. 새로운 스타일의 닫기 버튼
                Button(
                    onClick = onClose,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7CB342), // 하루마을 메인 그린
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "확인",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryFirstFinishDialogPreview() {
    MypatTheme {
        DiaryFirstFinishDialog(
            onClose = {},
        )
    }
}