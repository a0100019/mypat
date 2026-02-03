package com.a0100019.mypat.presentation.diary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DiaryEmotionDialog(
    onClose: () -> Unit,
    onEmotionClick: (String) -> Unit,
    removeEmotion: Boolean = false
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if(removeEmotion){
                    Text(
                        text = " 필터 감정을 선택해주세요",
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                    )
                } else {
                    Text(
                        text = " 오늘의 감정을 선택해주세요",
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        // 그림자를 먼저 넣어 바닥에서 떠 있는 느낌 유도
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color.Black.copy(alpha = 0.1f),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        )
                        .background(
                            color = Color(0xFFFDFDFD), // 아주 깨끗한 미색
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE0E0E0).copy(alpha = 0.5f), // 거의 보이지 않는 연한 회색 선
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(12.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(16.dp), // 여백을 조금 더 줘서 시원하게
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // 리스트로 관리하면 코드가 훨씬 깔끔해집니다
                        val emotions = listOf(
                            "smile", "exciting", "love", "thinking",
                            "neutral", "sad", "cry", "angry"
                        )

                        items(emotions) { emotion ->
                            val filePath = "emotion/$emotion.png"

                            // [감정 아이콘 개별 아이템]
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f) // 정사각형 유지
                                    .shadow(elevation = 2.dp, shape = CircleShape) // 아주 살짝 입체감
                                    .background(Color.White.copy(alpha = 0.8f), CircleShape) // 뽀얀 배경
                                    .border(1.dp, Color.White, CircleShape) // 깨끗한 테두리
                                    .clickable { onEmotionClick(filePath) }
                                    .padding(8.dp), // 아이콘과 배경 사이 간격
                                contentAlignment = Alignment.Center
                            ) {
                                JustImage(
                                    filePath = filePath,
                                    modifier = Modifier.fillMaxSize() // 박스 안에 꽉 차게
                                )
                            }
                        }

                        if (removeEmotion) {
                            item(span = { GridItemSpan(4) }) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // 구분선 하나 넣어주면 더 깔끔해요
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(3.dp)
                                            .background(Color.Gray.copy(alpha = 0.2f), CircleShape)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // [전체보기/취소 버튼]
                                    Surface(
                                        onClick = { onEmotionClick("emotion/allEmotion.png") },
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.9f),
                                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                                        shadowElevation = 4.dp
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            JustImage(
                                                filePath = "emotion/allEmotion.png",
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun DiaryEmotionDialogPreview() {
    MypatTheme {
        DiaryEmotionDialog(
            onClose = {},
            onEmotionClick = {},
            removeEmotion = true
        )
    }
}