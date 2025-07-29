package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
                .fillMaxWidth()
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

                Text(
                    text = "감정을 선택해주세요",
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.scrim,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                    //.fillMaxHeight(0.5f)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),    // 세로 간격
                        horizontalArrangement = Arrangement.spacedBy(24.dp),  // 가로 간격
                    ) {

                        item {
                            JustImage(
                                filePath = "emotion/smile.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/smile.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "emotion/exciting.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/exciting.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "emotion/love.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/love.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "emotion/thinking.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/thinking.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "emotion/neutral.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/neutral.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "emotion/sad.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/sad.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "emotion/cry.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/cry.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "emotion/angry.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/angry.png")
                                }
                            )
                        }

                        if (removeEmotion) {
                            item(span = { GridItemSpan(4) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    JustImage(
                                        filePath = "emotion/allEmotion.png",
                                        modifier = Modifier
                                            .size(40.dp) // ← 크기 고정
                                            .clickable {
                                                onEmotionClick("emotion/allEmotion.png")
                                            }
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