package com.a0100019.mypat.presentation.daily.knowledge

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.a0100019.mypat.data.room.knowledge.Knowledge
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun KnowledgeReadyDialog(
    onClose: () -> Unit = {},
    knowledgeData: Knowledge = Knowledge(),
    onTextChange: (String) -> Unit = {},
    onSubmitClick: () -> Unit = {},
    text: String = ""
) {

    Dialog(onDismissRequest = onClose) {

        Box(
            modifier = Modifier
                .width(340.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp))
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(28.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(20.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Spacer(modifier = Modifier.height(30.dp))

                /* ---------- 📘 제목 ---------- */
                Text(
                    text = knowledgeData.answer,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(30.dp))

                /* ---------- 의미 카드 (스크롤 영역) ---------- */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(306.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 8.dp)
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        item {
                            Text(
                                text = knowledgeData.meaning,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                /* ---------- ✍️ 복습 입력 ---------- */
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text("복습하기") },
                    placeholder = { Text("오늘 공부한 단어를 적어보세요") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                /* ---------- 버튼 ---------- */
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    MainButton(
                        onClick = onClose,
                        text = "닫기"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MainButton(
                        onClick = onSubmitClick,
                        text = "확인"
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun KnowledgeReadyDialogPreview() {
    MypatTheme {
        KnowledgeReadyDialog(
            onClose = {},
            knowledgeData = Knowledge(
                answer = "플라시보 효과",
                meaning = "약효가 전혀 없는 약을 먹고도 약효 때문에 병이 난 것과 같은 효과를 얻는 현상을 '플라시보 효과'라고 한다. 가짜약이란 뜻의 한자어를 써서 '위약 효과'라고도 한다. 플라시보 효과란, 생물학적으로는 아무런 효과가 없는 중성적인 물질이지만 그것이 효과가 있다고 믿는 사람들에게는 실제 효과가 나타나는 현상을 말한다."
            ),
        )
    }
}