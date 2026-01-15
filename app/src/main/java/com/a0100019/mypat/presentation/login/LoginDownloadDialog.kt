package com.a0100019.mypat.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun LoginDownloadDialog(
    onClose: () -> Unit,
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(30.dp, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 환영 아이콘 또는 캐릭터 (예시로 이모지/아이콘 사용)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pet),
                        contentDescription = "회전된 이미지",
                        modifier = Modifier
                            .size(30.dp)
                    )

                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. 메인 메시지
                Text(
                    text = "준비 완료!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. 서브 메시지
                Text(
                    text = "하루마을 데이터를 성공적으로 가져왔어요.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFE8E8), // 🌷 파스텔 핑크
                        contentColor = Color(0xFF6D4C41)    // 🌰 따뜻한 브라운 텍스트
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🌱",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = "시작하기",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }


                // 하단에 작게 문구 추가 (선택 사항)
                Text(
                    text = "Welcome to Haru Village",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginDownloadDialogPreview() {
    MypatTheme {
        LoginDownloadDialog(
            onClose = {},
        )
    }
}